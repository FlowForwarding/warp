package org.flowforwarding.of.controller.protocol;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericEnumSymbol;
import org.apache.avro.generic.GenericData.EnumSymbol;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;

public class Protocol {
	
	private final String schemaSrc = "src/main/avro/of_protocol.avsc";
	
	private Schema ofpHeaderSchema = null; 
	private Schema ofpSwitchFeaturesSchema = null;
	private Schema ofpSwitchConfigSchema = null; 
	private Schema ofpMatchSchema = null;
	private Schema ofpFlowModSchema = null;
	
	private Schema ofpTypeSchema = null;
	private Schema ofpConfigFlagsSchema = null;	
	private Schema ofpFlowModCommandSchema = null;
	private Schema ofpFlowModFlagsSchema = null;
	private Schema ofpMatchTypeSchema = null;

	private GenericEnumSymbol ofpTypes = null;
	
	
	
	public void init() {
		 
		Schema.Parser parser = new Schema.Parser();
		try {
			parser.parse(getClass().getResourceAsStream(this.schemaSrc));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		ofpHeaderSchema = parser.getTypes().get("of12.ofp_header");
		ofpSwitchFeaturesSchema = parser.getTypes().get("of12.ofp_switch_features");
		ofpSwitchConfigSchema = parser.getTypes().get("of12.ofp_switch_config");
		ofpMatchSchema = parser.getTypes().get("of12.ofp_match");
		ofpFlowModSchema = parser.getTypes().get("of12.ofp_flow_mod");
		ofpTypeSchema = parser.getTypes().get("of12.ofp_type");
		ofpConfigFlagsSchema = parser.getTypes().get("of12.ofp_config_flags");
		ofpMatchTypeSchema = parser.getTypes().get("of12.ofp_match_type");
		
		
		
	}
	
	public ByteArrayOutputStream getHello(ByteArrayOutputStream out) {
		
		GenericRecord ofpHeaderRecord = new GenericData.Record(ofpHeaderSchema);
		
		ByteBuffer versionBuffer = ByteBuffer.allocate(1);
		versionBuffer.put((byte)3);
		ofpHeaderRecord.put("version", versionBuffer);
		
		ofpHeaderRecord.put("type", new EnumSymbol(ofpTypeSchema, "OFPT_HELLO"));
		
		ByteBuffer lenBuffer = ByteBuffer.allocate(2);
		versionBuffer.put((byte)8);
		versionBuffer.put((byte)0);
		ofpHeaderRecord.put("length", lenBuffer);
		
		ByteBuffer xidBuffer = ByteBuffer.allocate(4);
		versionBuffer.put((byte)0);
		versionBuffer.put((byte)0);
		versionBuffer.put((byte)0);
		versionBuffer.put((byte)0);
		ofpHeaderRecord.put("xid", xidBuffer);

        DatumWriter<GenericRecord> writer = new GenericDatumWriter<GenericRecord>(ofpHeaderSchema);
	    
	    Encoder encoder = EncoderFactory.get().binaryNonEncoder(out, null);
	    
		try {
			writer.write(ofpHeaderRecord, encoder);
		    encoder.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return out;
	}
	
	public ByteArrayOutputStream getSwitchFeaturesRequest(ByteArrayOutputStream out) {
		
		GenericRecord ofpHeaderRecord = new GenericData.Record(ofpHeaderSchema);
		GenericRecord ofpSwitchFeaturesRecord = new GenericData.Record(ofpSwitchFeaturesSchema);
		
		ByteBuffer versionBuffer = ByteBuffer.allocate(1);
		versionBuffer.put((byte)3);
		ofpHeaderRecord.put("version", versionBuffer);
		
		ofpHeaderRecord.put("type", new EnumSymbol(ofpTypeSchema, "OFPT_FEATURES_REQUEST"));
		
		ByteBuffer lenBuffer = ByteBuffer.allocate(2);
		versionBuffer.put((byte)32);
		versionBuffer.put((byte)0);
		ofpHeaderRecord.put("length", lenBuffer);
		
		ByteBuffer xidBuffer = ByteBuffer.allocate(4);
		versionBuffer.put((byte)0);
		versionBuffer.put((byte)0);
		versionBuffer.put((byte)0);
		versionBuffer.put((byte)0);
		ofpHeaderRecord.put("xid", xidBuffer);
		
		ofpSwitchFeaturesRecord.put("header",ofpHeaderRecord);
		
		ByteBuffer datapathBuffer = ByteBuffer.allocate(8);
		versionBuffer.put((byte)0);
		versionBuffer.put((byte)0);
		versionBuffer.put((byte)0);
		versionBuffer.put((byte)0);
		versionBuffer.put((byte)0);
		versionBuffer.put((byte)0);
		versionBuffer.put((byte)0);
		versionBuffer.put((byte)0);
		ofpSwitchFeaturesRecord.put("datapath_id", datapathBuffer);
		
		ByteBuffer nBuffersBuffer = ByteBuffer.allocate(4);
		versionBuffer.put((byte)0);
		versionBuffer.put((byte)0);
		versionBuffer.put((byte)0);
		versionBuffer.put((byte)0);
		ofpSwitchFeaturesRecord.put("n_buffers", nBuffersBuffer);
		
		ByteBuffer nTablesBuffer = ByteBuffer.allocate(1);
		versionBuffer.put((byte)0);
		ofpSwitchFeaturesRecord.put("n_tables", nTablesBuffer);
		
		ByteBuffer nPadBuffer = ByteBuffer.allocate(3);
		versionBuffer.put((byte)0);
		versionBuffer.put((byte)0);
		versionBuffer.put((byte)0);
		ofpSwitchFeaturesRecord.put("pad", nTablesBuffer);
		
		ByteBuffer nCapabBuffer = ByteBuffer.allocate(2);
		nCapabBuffer.put((byte)0);
		nCapabBuffer.put((byte)0);
		ofpSwitchFeaturesRecord.put("capabilities", nCapabBuffer);
		
		ByteBuffer nReservedBuffer = ByteBuffer.allocate(2);
		nReservedBuffer.put((byte)0);
		nReservedBuffer.put((byte)0);
		ofpSwitchFeaturesRecord.put("reserved", nReservedBuffer);

        DatumWriter<GenericRecord> writer = new GenericDatumWriter<GenericRecord>(ofpSwitchFeaturesSchema);
	    
	    Encoder encoder = EncoderFactory.get().binaryNonEncoder(out, null);
	    
		try {
			writer.write(ofpSwitchFeaturesRecord, encoder);
		    encoder.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return out;
	}

	public ByteArrayOutputStream getSwitchConfigRequest(ByteArrayOutputStream out) {
		
		GenericRecord ofpHeaderRecord = new GenericData.Record(ofpHeaderSchema);
		GenericRecord ofpSwitchConfigRecord = new GenericData.Record(ofpSwitchConfigSchema);
		
		ByteBuffer versionBuffer = ByteBuffer.allocate(1);
		versionBuffer.put((byte)3);
		ofpHeaderRecord.put("version", versionBuffer);
		
		ofpHeaderRecord.put("type", new EnumSymbol(ofpTypeSchema, "OFPT_GET_CONFIG_REQUEST"));
		
		ByteBuffer lenBuffer = ByteBuffer.allocate(2);
		versionBuffer.put((byte)12);
		versionBuffer.put((byte)0);
		ofpHeaderRecord.put("length", lenBuffer);
		
		ByteBuffer xidBuffer = ByteBuffer.allocate(4);
		versionBuffer.put((byte)0);
		versionBuffer.put((byte)0);
		versionBuffer.put((byte)0);
		versionBuffer.put((byte)0);
		ofpHeaderRecord.put("xid", xidBuffer);

        ofpSwitchConfigRecord.put("header", ofpHeaderRecord);
        ofpSwitchConfigRecord.put("flags", new EnumSymbol(ofpConfigFlagsSchema, "OFPC_FRAG_NORMAL"));
        
        ByteBuffer missSendLenBuffer = ByteBuffer.allocate(2);
        missSendLenBuffer.put((byte)0);
        missSendLenBuffer.put((byte)0);
        ofpSwitchConfigRecord.put("miss_send_len", missSendLenBuffer);
	    
        DatumWriter<GenericRecord> writer = new GenericDatumWriter<GenericRecord>(ofpSwitchConfigSchema);
	    Encoder encoder = EncoderFactory.get().binaryNonEncoder(out, null);
	    
		try {
			writer.write(ofpSwitchConfigRecord, encoder);
		    encoder.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return out;
	}
	
	public ByteArrayOutputStream getFlowMod(ByteArrayOutputStream out) {
		
		GenericRecord ofpHeaderRecord = new GenericData.Record(ofpHeaderSchema);
		GenericRecord ofpFlowModRecord = new GenericData.Record(ofpFlowModSchema);
		GenericRecord ofpMatchRecord = new GenericData.Record(ofpMatchSchema);
		
		ByteBuffer versionBuffer = ByteBuffer.allocate(1);
		versionBuffer.put((byte)3);
		ofpHeaderRecord.put("version", versionBuffer);
		
		ofpHeaderRecord.put("type", new EnumSymbol(ofpTypeSchema, "OFPT_FLOW_MOD"));
		
		ByteBuffer lenBuffer = ByteBuffer.allocate(2);
		lenBuffer.put((byte)56);
		lenBuffer.put((byte)0);
		ofpHeaderRecord.put("length", lenBuffer);
		
		ByteBuffer xidBuffer = ByteBuffer.allocate(4);
		xidBuffer.put((byte)0);
		xidBuffer.put((byte)0);
		xidBuffer.put((byte)0);
		xidBuffer.put((byte)0);
		ofpHeaderRecord.put("xid", xidBuffer);

		ofpFlowModRecord.put("header", ofpHeaderRecord);
        
        ByteBuffer cookieBuffer = ByteBuffer.allocate(8);
        cookieBuffer.put((byte)0);
        cookieBuffer.put((byte)0);
        cookieBuffer.put((byte)0);
        cookieBuffer.put((byte)0);
        cookieBuffer.put((byte)0);
        cookieBuffer.put((byte)0);
        cookieBuffer.put((byte)0);
        cookieBuffer.put((byte)0);
        ofpFlowModRecord.put("cookie", cookieBuffer);
        
        ByteBuffer cookieMaskBuffer = ByteBuffer.allocate(8);
        cookieMaskBuffer.put((byte)0);
        cookieMaskBuffer.put((byte)0);
        cookieMaskBuffer.put((byte)0);
        cookieMaskBuffer.put((byte)0);
        cookieMaskBuffer.put((byte)0);
        cookieMaskBuffer.put((byte)0);
        cookieMaskBuffer.put((byte)0);
        cookieMaskBuffer.put((byte)0);
        ofpFlowModRecord.put("cookie_mask", cookieMaskBuffer);
        
        ByteBuffer tableIdBuffer = ByteBuffer.allocate(1);
        tableIdBuffer.put((byte)0);
        ofpFlowModRecord.put("table_id", tableIdBuffer);
        
        ofpFlowModRecord.put("command", new EnumSymbol(ofpFlowModCommandSchema, "OFPFC_MODIFY"));
        
        ByteBuffer idleTimeoutBuffer = ByteBuffer.allocate(2);
        idleTimeoutBuffer.put((byte)0);
        idleTimeoutBuffer.put((byte)0);
        ofpFlowModRecord.put("idle_timeout", idleTimeoutBuffer);

        ByteBuffer hardTimeoutBuffer = ByteBuffer.allocate(2);
        hardTimeoutBuffer.put((byte)0);
        hardTimeoutBuffer.put((byte)0);
        ofpFlowModRecord.put("hard_timeout", hardTimeoutBuffer);
        
        ByteBuffer priorityBuffer = ByteBuffer.allocate(2);
        priorityBuffer.put((byte)0);
        priorityBuffer.put((byte)0);
        ofpFlowModRecord.put("priority", priorityBuffer);
        
        ByteBuffer bufferIdBuffer = ByteBuffer.allocate(4);
        bufferIdBuffer.put((byte)0);
        bufferIdBuffer.put((byte)0);
        bufferIdBuffer.put((byte)0);
        bufferIdBuffer.put((byte)0);
        ofpFlowModRecord.put("buffer_id", bufferIdBuffer);
        
        ByteBuffer outPortBuffer = ByteBuffer.allocate(4);
        outPortBuffer.put((byte)0);
        outPortBuffer.put((byte)0);
        outPortBuffer.put((byte)0);
        outPortBuffer.put((byte)0);
        ofpFlowModRecord.put("out_port", outPortBuffer);
        
        ByteBuffer outGroupBuffer = ByteBuffer.allocate(4);
        outGroupBuffer.put((byte)0);
        outGroupBuffer.put((byte)0);
        outGroupBuffer.put((byte)0);
        outGroupBuffer.put((byte)0);
        ofpFlowModRecord.put("out_group", outGroupBuffer);
        
        ofpFlowModRecord.put("flags", new EnumSymbol(ofpFlowModFlagsSchema, "OFPFF_SEND_FLOW_REM"));
        
        ByteBuffer padBuffer = ByteBuffer.allocate(2);
        padBuffer.put((byte)0);
        padBuffer.put((byte)0);
        ofpFlowModRecord.put("pad", padBuffer);
        
        ofpMatchRecord.put("type", new EnumSymbol(ofpMatchTypeSchema, "OFPMT_OXM"));
        
        ByteBuffer matchLenBuffer = ByteBuffer.allocate(2);
        matchLenBuffer.put((byte)4);
        matchLenBuffer.put((byte)0);
        ofpMatchRecord.put("length", matchLenBuffer);
        
        ByteBuffer oxmBuffer = ByteBuffer.allocate(4);
        oxmBuffer.put((byte)0);
        oxmBuffer.put((byte)0);
        oxmBuffer.put((byte)0);
        oxmBuffer.put((byte)0);
        ofpMatchRecord.put("oxm_fields", oxmBuffer);
        
        ofpFlowModRecord.put("match", ofpMatchRecord);
	    
        DatumWriter<GenericRecord> writer = new GenericDatumWriter<GenericRecord>(ofpFlowModSchema);
	    Encoder encoder = EncoderFactory.get().binaryNonEncoder(out, null);
	    
		try {
			writer.write(ofpFlowModRecord, encoder);
		    encoder.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return out;
	}

}
