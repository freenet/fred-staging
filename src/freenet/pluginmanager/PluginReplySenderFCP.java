/* This code is part of Freenet. It is distributed under the GNU General
 * Public License, version 2 (or at your option any later version). See
 * http://www.gnu.org/ for further details of the GPL. */
package freenet.pluginmanager;

import freenet.node.fcp.FCPCallFailedException;
import freenet.node.fcp.FCPConnectionHandler;
import freenet.node.fcp.FCPPluginReply;
import freenet.support.SimpleFieldSet;
import freenet.support.api.Bucket;

/**
 * @author saces
 * @author xor (xor@freenetproject.org)
 */
public class PluginReplySenderFCP extends PluginReplySender {
	
	final FCPConnectionHandler handler; 

	/**
	 * @see PluginReplySender#PluginReplySender(String, String, String)
	 */
	public PluginReplySenderFCP(FCPConnectionHandler handler2, String pluginname2, String clientIdentifier, String clientSideIdentifier) {
		super(pluginname2, clientIdentifier, clientSideIdentifier);
		
		handler = handler2;
	}

	@Override
	public void send(SimpleFieldSet params, Bucket bucket) throws PluginNotFoundException {
		// like in linux everthing is a file, in Plugintalker everything is a plugin. So it throws PluginNotFoundException
		// instead fcp connection errors 
		if (handler.isClosed()) throw new PluginNotFoundException("FCP connection closed");
		FCPPluginReply reply = new FCPPluginReply(pluginname, clientSideIdentifier, params, bucket);
		handler.outputHandler.queue(reply);
	}

    @Override
    public void sendSynchronous(SimpleFieldSet params, Bucket bucket) throws FCPCallFailedException {
        throw new UnsupportedOperationException("FIXME: Implement!");
    }
}
