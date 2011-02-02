package wikidev.server;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import wikidev.server.data.App;
import wikidev.server.data.Resource;
import wikidev.server.data.Version;

public class ResourceServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

			String[] part = request.getPathInfo().split("/");
			// note this starts with a / so [0] is not useful
			
			String app = null;
			String version = null;
			String resource = null;
			
			switch ( part.length ){
			case 0:
			case 1:
			case 2:
				// error we need at least app/resource
				break;
			case 3:
				// assume app/resource
				app = part[1];
				version = ""; // note we go for default version if not found...
				resource = part[2];
				break;
			case 4:
			default:
				// assume app/version/resource
				app = part[1];
				version = part[2];
				resource = part[3];
			}
		
			// find the resource in the database?
			try{
				App a = App.findApp( app );
				Version v = a.getVersion( version );
				if ( v == null )
					v= a.getLatestStable();
				if ( v == null ){
						response.sendError( 404 );
					return;
				}
				Resource r = v.getResource( resource );
				if ( r == null ){
					response.sendError( 404 );
					return;		
				}
				
				response.setContentType( r.getRevision().getMimeType() );
				if ( v.isStable() ){
					 response.setHeader("Cache-Control", "max-age=360000");
				}else{	
					response.setHeader("Cache-Control", "no-cache");
				}
			
				InputStream in = r.getRevision().getDataInputStream();
				if ( in == null ){
					response.sendError( 500 );
					return;
				}
				ServletOutputStream out = response.getOutputStream();
				byte[] buffer = new byte[1024 * 10];
				int len;
				while ((len = in.read(buffer)) != -1) {
				    out.write(buffer, 0, len);
				}
				out.close();
				in.close();
				
			}catch(Exception e ){
				response.sendError( 500 );
				return;
			}			
	}


	@SuppressWarnings("unchecked")
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String[] part = request.getPathInfo().split("/");
		// note this starts with a / so [0] is not useful
		
		String app = null;
		String version = null;
		String resource = null;
		
		switch ( part.length ){
		case 0:
		case 1:
		case 2:
		case 3:
			return;
		case 4:
		default:
			// assume app/version/resource
			app = part[1];
			version = part[2];
			resource = part[3];
		}
	
		// find the resource in the database?
		App a = App.findApp( app );
		Version v = a.getVersion( version );
		if ( v == null ){
			response.sendError( 404 );
			return;
		}
		Resource r = v.getResource( resource );
		
		
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		
		if ( isMultipart ){
			// Create a factory for disk-based file items
			FileItemFactory factory = new DiskFileItemFactory();
	
			// Create a new file upload handler
			ServletFileUpload upload = new ServletFileUpload(factory);
	
			// Parse the request
			try{
				List<FileItem> items = upload.parseRequest(request);
				
				for( FileItem fi : items ){
					if ( fi.getFieldName().equalsIgnoreCase("data") ){
						if ( r == null ){
							// need to create.
							v.createResource( resource , fi.getContentType(), fi.getInputStream() , Auth.getCurrentUser( request )  );
						}else{
							// doing an update
							r.update(fi.getContentType(), fi.getInputStream() , Auth.getCurrentUser( request ) );
						}
					}
				}
			} catch (FileUploadException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
