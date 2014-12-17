/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.controller.util

import java.io.{FileInputStream, File, DataInputStream, IOException}

class NonCachingClassLoader(classFilter: String => Boolean)(implicit parent: ClassLoader)  extends ClassLoader(parent){

  protected def classNameToPath(name: String): String =
    if (name endsWith ".class") name
    else name.replace('.', '/') + ".class"

  private val syncObj = new AnyRef
  private val classes = scala.collection.mutable.Map[String, Class[_]]()

  private def fileContent(path: String) = {
    val fileURL = parent.getResource(path)
    if(fileURL == null)
      throw new IOException("Unable to get resource " + path)
    val file = new File(fileURL.getFile)
    val data = Array.ofDim[Byte](file.length.toInt)
    val dis = new DataInputStream(new FileInputStream(file))
    dis.readFully(data)
    dis.close()
    data
  }

  override def loadClass(name: String, resolve: Boolean): Class[_] =
    syncObj synchronized {
      classes.get(name) match {
        case Some(c) => c
        case None if classFilter(name) =>
          val classData =
            try {
              fileContent(classNameToPath(name))
            } catch {
              case e: IOException =>
                throw new ClassNotFoundException("Could not read class file", e)
            }
          val result = defineClass(name, classData, 0, classData.length)
          if (result == null) throw new ClassFormatError()
          else {
            if (resolve) resolveClass(result)
            classes.put(name, result)
            result
          }
        case _ => parent.loadClass(name)
      }
    }
}