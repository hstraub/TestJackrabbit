import java.util.ArrayList;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.jackrabbit.webdav.DavConstants;
import org.apache.jackrabbit.webdav.MultiStatus;
import org.apache.jackrabbit.webdav.MultiStatusResponse;
import org.apache.jackrabbit.webdav.client.methods.DavMethod;
import org.apache.jackrabbit.webdav.client.methods.PropFindMethod;
import org.apache.jackrabbit.webdav.property.DavPropertyIterator;
import org.apache.jackrabbit.webdav.property.DavPropertySet;
import org.apache.jackrabbit.webdav.property.DefaultDavProperty;


public class Test1 {
	public static void main(String args[]) throws Exception {
		String webdavUrl = args[0]; // https://....
		String username = args[1];
		String password = args[2];
		
		Protocol easyhttps = new Protocol( "https", new EasySSLProtocolSocketFactory( ), 443 );
		Protocol.registerProtocol( "https", easyhttps );
		
		HostConfiguration hostConfig = new HostConfiguration( );
		hostConfig.setHost( webdavUrl );
		HttpConnectionManager connectionManager = new
				MultiThreadedHttpConnectionManager( );
		HttpConnectionManagerParams params = new HttpConnectionManagerParams( );
		int maxHostConnections = 20;
		params.setMaxConnectionsPerHost( hostConfig, maxHostConnections );
		connectionManager.setParams( params );
		HttpClient client = new HttpClient( connectionManager );
		Credentials creds = new UsernamePasswordCredentials( username, password );
		client.getState( ).setCredentials( AuthScope.ANY, creds );
		client.setHostConfiguration( hostConfig );
		
		DavMethod pFind = new PropFindMethod( webdavUrl,
				DavConstants.PROPFIND_ALL_PROP, DavConstants.DEPTH_1 );
		client.executeMethod( pFind );
		
		MultiStatus multiStatus = pFind.getResponseBodyAsMultiStatus( );
		MultiStatusResponse[] responses = multiStatus.getResponses( );
		MultiStatusResponse currResponse;
		ArrayList files = new ArrayList( );
		System.out.println( "Folders and Files in " + "/" + ":" );
		for ( int i = 0; i < responses.length; i++ ) {
			currResponse = responses[i];
			if ( ! ( currResponse.getHref( ).equals( "/" ) ) ) {
				System.out.println( currResponse.getHref( ) );
				DavPropertySet props = currResponse.getProperties( 200 );
				for ( DavPropertyIterator iter = props.iterator( ); iter.hasNext( ); ) {
					DefaultDavProperty tmp = ( DefaultDavProperty )iter.next( );
					System.out.println( tmp.getName( ) + " : " + tmp.getValue( ) );
				}
			}
		}
		
	}
}
