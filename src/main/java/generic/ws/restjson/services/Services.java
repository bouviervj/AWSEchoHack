package generic.ws.restjson.services;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;

import org.apache.log4j.Logger;

@Path("/v1")
public class Services {

	protected static final Logger LOGGER = Logger.getLogger(Services.class.getName());
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/simple")
	public String[] simple( @Context SecurityContext sc ) {
		LOGGER.info("Returning result ...");
		return new String[]{"This is a test"};
	}
		
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/simpleArg/{path}")
	public String[] simpleArg( @Context SecurityContext sc, @PathParam("path") String iPath  ) {
		LOGGER.info("Executing simple code with argument :"+iPath);
		return new String[]{"This is a test"};
	}
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/simplePost")
	public Object simplePost( @Context SecurityContext sc,
									   String[] iList) { 
		return null;
	}
	
	@GET
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	@Path("/{state}/otherMIME/")
	public byte[] simpleOtherMIMEType( @Context SecurityContext sc, @PathParam("state") String iState) throws Exception {
		try {
			return "Stream".getBytes();
		} catch (Exception e) {
			LOGGER.info("Simple Error");
			throw new NotFoundException(e.getMessage());		
		}
		
	}
	
	
}
