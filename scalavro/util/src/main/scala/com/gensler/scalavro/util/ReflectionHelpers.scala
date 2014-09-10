package com.gensler.scalavro.util

import scala.collection.immutable.ListMap
import scala.reflect.api.{ Universe, Mirror, TypeCreator }
import scala.reflect.ClassTag
import scala.reflect.runtime.universe._
import scala.collection.mutable.Builder

import com.typesafe.config.{ Config, ConfigFactory }

/**
  * Companion object for [[ReflectionHelpers]]
  */
object ReflectionHelpers extends ReflectionHelpers

trait ReflectionHelpers extends Logging {

  protected[scalavro] val classLoaderMirror = runtimeMirror(getClass.getClassLoader)

  protected[scalavro] def isSubclassOf[Tested: TypeTag, Base: TypeTag] =
    typeTag[Tested].tpe.typeSymbol.asClass.baseClasses contains typeTag[Base].tpe.typeSymbol

  def getCompanionObject(t: Type) = {
    val objModule = t.typeSymbol.companionSymbol.asModule
    val companionMirror = classLoaderMirror.reflectModule(objModule)
    companionMirror.instance
  }

  /**
    * Returns a sequence of Strings, each of which names a value of the
    * supplied enumeration type.
    */
  protected[scalavro] def symbolsOf[E <: Enumeration: TypeTag]: Seq[String] = {
    val valueType = typeOf[E#Value]

    val isValueType = (sym: Symbol) => {
      !sym.isMethod && !sym.isType &&
        sym.typeSignature.baseType(valueType.typeSymbol) =:= valueType
    }

    typeOf[E].members.collect {
      case sym: Symbol if isValueType(sym) => sym.name.toString.trim
    }.toSeq.reverse
  }

  /**
    * Returns a type tag for the parent `scala.Enumeration` of the supplied
    * enumeration value type.
    */
  protected[scalavro] def enumForValue[V <: Enumeration#Value: TypeTag]: TypeTag[_ <: Enumeration] = {
    val TypeRef(enclosing, _, _) = typeOf[V]
    tagForType(enclosing).asInstanceOf[TypeTag[_ <: Enumeration]]
  }

  private lazy val reflections = {
    import org.reflections.Reflections
    import org.reflections.util.{ ConfigurationBuilder, FilterBuilder }
    import org.reflections.scanners.SubTypesScanner
    import scala.collection.JavaConversions._

    val config = ConfigFactory.load.getConfig("com.gensler.scalavro").withFallback(
      ConfigFactory.parseString("""
        reflections-excluded-packages = [
          "java",
          "javax",
          "scala",
          "com.gensler.scalavro.io",
          "com.gensler.scalavro.types"
        ]
      """)
    )

    val reflectionsExcludedPackages = config.getStringList("reflections-excluded-packages")

    log.debug(
      "Reflections class loader scanner will ignore the following packages:\n    {}\n",
      reflectionsExcludedPackages.mkString("\n    ")
    )

    val classFilter = new FilterBuilder
    reflectionsExcludedPackages.foreach { packageName => classFilter.excludePackage(packageName) }

    val urls = this.getClass.getClassLoader.asInstanceOf[java.net.URLClassLoader].getURLs

    val configBuilder = new ConfigurationBuilder
    configBuilder setUrls (urls: _*)
    configBuilder.useParallelExecutor() // scan using # available processors
    configBuilder filterInputsBy classFilter
    configBuilder setScanners new SubTypesScanner(false)

    configBuilder.build
  }

  /**
    * Returns `true` iff the supplied class symbol corresponds to a
    * serializable type.
    */
  protected[scalavro] def classSymbolIsTypeable(sym: ClassSymbol): Boolean = {

    val symType = sym.selfType

    sym.isPrimitive ||
      sym.isAbstract || sym.isTrait ||
      (sym.isCaseClass && sym.typeParams.isEmpty) ||
      symType <:< typeOf[Set[_]] ||
      symType <:< typeOf[Map[String, _]] ||
      symType <:< typeOf[Seq[_]] ||
      symType.baseClasses.head.owner == typeOf[Enumeration].typeSymbol ||
      classLoaderMirror.runtimeClass(symType.typeSymbol.asClass).isEnum ||
      symType <:< typeOf[FixedData] ||
      symType <:< typeOf[Either[_, _]] ||
      symType <:< typeOf[Option[_]] ||
      symType <:< typeOf[Union.not[_]] ||
      symType <:< typeOf[Union[_]]
  }

  /**
    * Returns a TypeTag for each currently loaded avro-typeable subtype of
    * the supplied type.
    */
  protected[scalavro] def typeableSubTypesOf[T: TypeTag]: Seq[TypeTag[_]] = {
    import scala.collection.JavaConversions.asScalaSet
    import java.lang.reflect.Modifier

    val tType = typeOf[T]
    val tSym = typeOf[T].typeSymbol

    if (!tSym.isClass) Seq()

    else if (tSym.asClass.isSealed) {
      tSym.asClass.knownDirectSubclasses.collect {
        case sym: Symbol if (
          sym.isClass &&
          !sym.asClass.isAbstract &&
          classSymbolIsTypeable(sym.asClass)
        ) => tagForType(sym.asClass.selfType)
      }.toSeq
    }

    else {
      // filter out abstract classes
      val subClassSymbols = asScalaSet(
        reflections.getSubTypesOf(classLoaderMirror.runtimeClass(typeOf[T]))
      ).collect {
          case clazz: Class[_] if !Modifier.isAbstract(clazz.getModifiers) =>
            classLoaderMirror classSymbol clazz
        }

      // filter class symbols to include only avro-typable classes
      subClassSymbols.collect {
        case sym: ClassSymbol if classSymbolIsTypeable(sym) => tagForType(sym.selfType)
      }.toSeq
    }
  }

  /**
    * Returns a map from formal parameter names to type tags, containing one
    * mapping for each constructor argument.  The resulting map (a ListMap)
    * preserves the order of the primary constructor's parameter list.
    */
  protected[scalavro] def caseClassParamsOf[T: TypeTag]: ListMap[String, TypeTag[_]] = {
    val tpe = typeOf[T]
    val constructorSymbol = tpe.decl(termNames.CONSTRUCTOR)
    val defaultConstructor =
      if (constructorSymbol.isMethod) constructorSymbol.asMethod
      else {
        val ctors = constructorSymbol.asTerm.alternatives
        ctors.map { _.asMethod }.find { _.isPrimaryConstructor }.get
      }

    ListMap[String, TypeTag[_]]() ++ defaultConstructor.paramLists.reduceLeft(_ ++ _).map {
      sym => sym.name.toString -> tagForType(tpe.member(sym.name).asMethod.returnType)
    }
  }

  /**
    * Returns Some(MethodMirror) for the public construcor of the supplied
    * class type that takes the supplied argument type as its only parameter.
    *
    * Returns None if no suitable public single-argument constructor can
    * be found for the supplied type.
    *
    * @tparam T the type of the class to inspect for a suitable single-argument
    *           constructor
    * @tparam A the type of the constructor's formal parameter
    */
  protected[scalavro] def singleArgumentConstructor[T: TypeTag, A: TypeTag]: Option[MethodMirror] = {

    val classType = typeOf[T]
    val targetArgType = typeOf[A]
    val constructorSymbol = classType.decl(termNames.CONSTRUCTOR)

    def isAccessibleAndMatchesArgument(methodSymbol: MethodSymbol): Boolean = {
      val privateWithin = methodSymbol.asMethod.privateWithin.fullName
      (methodSymbol.isPublic || !methodSymbol.isPrivate && !methodSymbol.isProtected && privateWithin == "com.gensler.scalavro") && {
        methodSymbol.asMethod.paramLists match {
          case List(List(argSym)) => argSym.typeSignatureIn(classType) =:= targetArgType
          case _                  => false
        }
      }
    }

    val singleArgumentConstructor: Option[MethodSymbol] =
      if (constructorSymbol.isMethod && isAccessibleAndMatchesArgument(constructorSymbol.asMethod)) {
        Some(constructorSymbol.asMethod)
      }
      else {
        val ctors = constructorSymbol.asTerm.alternatives
        ctors.map { _.asMethod }.find { isAccessibleAndMatchesArgument }
      }

    singleArgumentConstructor.collect {
      case constructorSymbol: MethodSymbol if classType.typeSymbol.isClass => {
        val classMirror = classLoaderMirror reflectClass classType.typeSymbol.asClass
        classMirror reflectConstructor constructorSymbol
      }
    }
  }

  /**
    * Returns a ClassTag from the current class loader mirror for the supplied
    * type.
    */
  protected[scalavro] def classTagForType(tpe: Type): ClassTag[_] =
    ClassTag(classLoaderMirror runtimeClass tpe)

  /**
    * Returns a TypeTag in the current runtime universe for the supplied type.
    */
  protected[scalavro] def tagForType(tpe: Type): TypeTag[_] = TypeTag(
    classLoaderMirror,
    new TypeCreator {
      def apply[U <: Universe with Singleton](m: Mirror[U]) = tpe.asInstanceOf[U#Type]
    }
  )

  /**
    * Returns Success(methodMirror) for a varargs factory method derived from
    * the supplied type's companion object, if one can be derived.  Returns a
    * Failure otherwise.
    */
  protected[scalavro] def varargsFactory[T: TypeTag]: scala.util.Try[(Any*) => T] = scala.util.Try {

    val tpe = typeOf[T]

    lazy val varargsApply = companionVarargsApply[T]
    lazy val builderFactory = companionBuilderFactory[T]
    // lazy val fromSeq = companionFromSeqFactory[T]

    if (varargsApply.isDefined) {
      def factory(args: Any*): T = varargsApply.get.apply(args).asInstanceOf[T]
      factory
    }
    else if (builderFactory.isDefined) {
      def factory(args: Any*): T = {
        val builder = builderFactory.get.apply().asInstanceOf[Builder[Any, _]]
        builder ++= args
        builder.result.asInstanceOf[T]
      }
      factory
    }
    else throw new IllegalArgumentException(
      """
        |Searched the companion object for one of the following:
        |  - a public varargs apply method
        |  - a public Builder-valued 0-argument method
        |
        |but no such method was found for type [%s]!"
      """.format(tpe).stripMargin
    )
  }

  /**
    * Wraps information about a companion object for a type.
    */
  protected[this] case class CompanionMetadata[T](
    symbol: ModuleSymbol,
    instance: Any,
    instanceMirror: InstanceMirror,
    classType: Type)

  object CompanionMetadata {
    /**
      * Returns a Some wrapping CompanionMetadata for the supplied class type, if
      * that class type has a companion, and None otherwise.
      */
    def apply[T: TypeTag]: Option[CompanionMetadata[T]] = {

      val typeSymbol = typeOf[T].typeSymbol

      val companionSymbol: Option[ModuleSymbol] = {
        if (!typeSymbol.isClass) None // supplied type is not a class
        else {
          val classSymbol = typeSymbol.asClass
          if (!classSymbol.companion.isModule) None // supplied class type has no companion
          else Some(classSymbol.companion.asModule)
        }
      }

      companionSymbol.map { symbol =>
        val instance = classLoaderMirror.reflectModule(symbol).instance
        val instanceMirror = classLoaderMirror reflect instance
        val classType = symbol.moduleClass.asClass.asType.toType
        CompanionMetadata(symbol, instance, instanceMirror, classType)
      }
    }
  }

  /**
    * Returns Some(methodMirror) for the public varargs apply method of the
    * supplied type's companion object, if one exists.  Returns None otherwise.
    */
  protected[this] def companionVarargsApply[T: TypeTag]: Option[MethodMirror] = {

    def publicVarargs(ms: MethodSymbol): Boolean = ms.isPublic && ms.isVarargs

    CompanionMetadata[T].flatMap { companion =>
      val applySymbol: Option[MethodSymbol] = {
        val symbol = companion.classType.member(TermName("apply"))
        if (symbol.isMethod) {
          val methodSymbol = symbol.asMethod
          if (publicVarargs(methodSymbol)) Some(methodSymbol)
          else None
        }
        else {
          if (symbol.isTerm) {
            val choices = symbol.asTerm.alternatives
            choices.view.filter(_.isMethod).map(_.asMethod).find(publicVarargs)
          }
          else None
        }
      }

      applySymbol.map { apply => companion.instanceMirror reflectMethod apply }
    }
  }

  /**
    * Returns Some(methodMirror) for the Builder-valued 0-argument method of
    * the supplied type's companion object, if one exists.  Returns None
    * otherwise.
    */
  protected[this] def companionBuilderFactory[T: TypeTag]: Option[MethodMirror] = {
    CompanionMetadata[T].flatMap { companion =>
      val newBuilderSymbol = companion.classType.decls.toTraversable.find { symbol =>
        symbol.isMethod && {
          val methodSymbol = symbol.asMethod
          val isNullary = methodSymbol.paramLists == List()
          val returnsBuilder = methodSymbol.returnType.typeConstructor =:= typeOf[Builder[_, _]].typeConstructor
          isNullary && returnsBuilder
        }
      }

      newBuilderSymbol.map { newBuilder => companion.instanceMirror reflectMethod newBuilder.asMethod }
    }
  }

  /**
    * Provides access to named members of instances of the supplied type `P`.
    *
    * @tparam P         the type of the product instance in question
    * @tparam T         the expected type of the member value
    * @param membername the name of the member value to extract
    */
  class ProductElementExtractor[P: TypeTag, T: TypeTag](memberName: String) {
    val memberField = typeOf[P].decl(TermName(memberName)).asTerm.accessed.asTerm
    implicit val ct = ClassTag[P](classLoaderMirror runtimeClass typeOf[P])

    /**
      * Attempts to fetch the value from the supplied product instance.
      *
      * @param product    an instance of some product type, P
      */
    def extractFrom(product: P): T = {
      val instanceMirror = classLoaderMirror reflect product
      val fieldValue = instanceMirror reflectField memberField
      fieldValue.get.asInstanceOf[T]
    }
  }

  /**
    * Encapsulates functionality to reflectively invoke the constructor
    * for a given case class type `T`.
    *
    * @tparam T the type of the case class this factory builds
    */
  class CaseClassFactory[T: TypeTag] {

    val tpe = typeOf[T]
    val classSymbol = tpe.typeSymbol.asClass

    if (!(tpe <:< typeOf[Product] && classSymbol.isCaseClass))
      throw new IllegalArgumentException(
        "CaseClassFactory only applies to case classes!"
      )

    val classMirror = classLoaderMirror reflectClass classSymbol

    val constructorSymbol = tpe.decl(termNames.CONSTRUCTOR)

    val defaultConstructor =
      if (constructorSymbol.isMethod) constructorSymbol.asMethod
      else {
        val ctors = constructorSymbol.asTerm.alternatives
        ctors.map { _.asMethod }.find { _.isPrimaryConstructor }.get
      }

    val constructorMethod = classMirror reflectConstructor defaultConstructor

    /**
      * Attempts to create a new instance of the specified type by calling the
      * constructor method with the supplied arguments.
      *
      * @tparam T   the type of object to construct, which must be a case class
      * @param args the arguments to supply to the constructor method
      */
    def buildWith(args: Seq[_]): T = constructorMethod(args: _*).asInstanceOf[T]

  }

}
