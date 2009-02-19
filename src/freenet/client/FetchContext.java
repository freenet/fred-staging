/* This code is part of Freenet. It is distributed under the GNU General
 * Public License, version 2 (or at your option any later version). See
 * http://www.gnu.org/ for further details of the GPL. */
package freenet.client;

import java.util.Set;

import com.db4o.ObjectContainer;

import freenet.client.async.BlockSet;
import freenet.client.events.ClientEventProducer;
import freenet.client.events.SimpleEventProducer;
import freenet.support.api.BucketFactory;

/** Context for a Fetcher. Contains all the settings a Fetcher needs to know about. */
public class FetchContext implements Cloneable {

	public static final int IDENTICAL_MASK = 0;
	public static final int SPLITFILE_DEFAULT_BLOCK_MASK = 1;
	public static final int SPLITFILE_DEFAULT_MASK = 2;
	public static final int SET_RETURN_ARCHIVES = 4;
	/** Low-level client to send low-level requests to. */
	public long maxOutputLength;
	public long maxTempLength;
	public int maxRecursionLevel;
	public int maxArchiveRestarts;
	public int maxArchiveLevels;
	public boolean dontEnterImplicitArchives;
	public int maxSplitfileThreads;
	public int maxSplitfileBlockRetries;
	public int maxNonSplitfileRetries;
	public int maxUSKRetries;
	public boolean allowSplitfiles;
	public boolean followRedirects;
	public boolean localRequestOnly;
	public boolean ignoreStore;
	public final ClientEventProducer eventProducer;
	public int maxMetadataSize;
	public int maxDataBlocksPerSegment;
	public int maxCheckBlocksPerSegment;
	public boolean cacheLocalRequests;
	/** If true, and we get a ZIP manifest, and we have no meta-strings left, then
	 * return the manifest contents as data. */
	public boolean returnZIPManifests;
	public final boolean ignoreTooManyPathComponents;
	/** If set, contains a set of blocks to be consulted before checking the datastore. */
	public final BlockSet blocks;
	public Set allowedMIMETypes;
	private final boolean hasOwnEventProducer;
	
	public FetchContext(long curMaxLength, 
			long curMaxTempLength, int maxMetadataSize, int maxRecursionLevel, int maxArchiveRestarts, int maxArchiveLevels,
			boolean dontEnterImplicitArchives, int maxSplitfileThreads,
			int maxSplitfileBlockRetries, int maxNonSplitfileRetries, int maxUSKRetries,
			boolean allowSplitfiles, boolean followRedirects, boolean localRequestOnly,
			int maxDataBlocksPerSegment, int maxCheckBlocksPerSegment,
			BucketFactory bucketFactory,
			ClientEventProducer producer, boolean cacheLocalRequests, 
			boolean ignoreTooManyPathComponents) {
		this.blocks = null;
		this.maxOutputLength = curMaxLength;
		this.maxTempLength = curMaxTempLength;
		this.maxMetadataSize = maxMetadataSize;
		this.maxRecursionLevel = maxRecursionLevel;
		this.maxArchiveRestarts = maxArchiveRestarts;
		this.maxArchiveLevels = maxArchiveLevels;
		this.dontEnterImplicitArchives = dontEnterImplicitArchives;
		this.maxSplitfileThreads = maxSplitfileThreads;
		this.maxSplitfileBlockRetries = maxSplitfileBlockRetries;
		this.maxNonSplitfileRetries = maxNonSplitfileRetries;
		this.maxUSKRetries = maxUSKRetries;
		this.allowSplitfiles = allowSplitfiles;
		this.followRedirects = followRedirects;
		this.localRequestOnly = localRequestOnly;
		this.eventProducer = producer;
		this.maxDataBlocksPerSegment = maxDataBlocksPerSegment;
		this.maxCheckBlocksPerSegment = maxCheckBlocksPerSegment;
		this.cacheLocalRequests = cacheLocalRequests;
		this.ignoreTooManyPathComponents = ignoreTooManyPathComponents;
		hasOwnEventProducer = true;
	}

	/** Copy a FetchContext.
	 * @param ctx
	 * @param maskID
	 * @param keepProducer
	 * @param blocks Storing a BlockSet to the database is not supported, see comments on SimpleBlockSet.objectCanNew().
	 */
	public FetchContext(FetchContext ctx, int maskID, boolean keepProducer, BlockSet blocks) {
		if(keepProducer)
			this.eventProducer = ctx.eventProducer;
		else
			this.eventProducer = new SimpleEventProducer();
		hasOwnEventProducer = !keepProducer;
		this.ignoreTooManyPathComponents = ctx.ignoreTooManyPathComponents;
		if(blocks != null)
			this.blocks = blocks;
		else
			this.blocks = ctx.blocks;
		this.allowedMIMETypes = ctx.allowedMIMETypes;
		this.maxUSKRetries = ctx.maxUSKRetries;
		if(maskID == IDENTICAL_MASK) {
			this.maxOutputLength = ctx.maxOutputLength;
			this.maxMetadataSize = ctx.maxMetadataSize;
			this.maxTempLength = ctx.maxTempLength;
			this.maxRecursionLevel = ctx.maxRecursionLevel;
			this.maxArchiveRestarts = ctx.maxArchiveRestarts;
			this.maxArchiveLevels = ctx.maxArchiveLevels;
			this.dontEnterImplicitArchives = ctx.dontEnterImplicitArchives;
			this.maxSplitfileThreads = ctx.maxSplitfileThreads;
			this.maxSplitfileBlockRetries = ctx.maxSplitfileBlockRetries;
			this.maxNonSplitfileRetries = ctx.maxNonSplitfileRetries;
			this.allowSplitfiles = ctx.allowSplitfiles;
			this.followRedirects = ctx.followRedirects;
			this.localRequestOnly = ctx.localRequestOnly;
			this.maxDataBlocksPerSegment = ctx.maxDataBlocksPerSegment;
			this.maxCheckBlocksPerSegment = ctx.maxCheckBlocksPerSegment;
			this.cacheLocalRequests = ctx.cacheLocalRequests;
			this.returnZIPManifests = ctx.returnZIPManifests;
		} else if(maskID == SPLITFILE_DEFAULT_BLOCK_MASK) {
			this.maxOutputLength = ctx.maxOutputLength;
			this.maxMetadataSize = ctx.maxMetadataSize;
			this.maxTempLength = ctx.maxTempLength;
			this.maxRecursionLevel = 1;
			this.maxArchiveRestarts = 0;
			this.maxArchiveLevels = ctx.maxArchiveLevels;
			this.dontEnterImplicitArchives = true;
			this.maxSplitfileThreads = 0;
			this.maxSplitfileBlockRetries = ctx.maxSplitfileBlockRetries;
			this.maxNonSplitfileRetries = ctx.maxSplitfileBlockRetries;
			this.allowSplitfiles = false;
			this.followRedirects = false;
			this.localRequestOnly = ctx.localRequestOnly;
			this.maxDataBlocksPerSegment = 0;
			this.maxCheckBlocksPerSegment = 0;
			this.cacheLocalRequests = ctx.cacheLocalRequests;
			this.returnZIPManifests = false;
		} else if(maskID == SPLITFILE_DEFAULT_MASK) {
			this.maxOutputLength = ctx.maxOutputLength;
			this.maxTempLength = ctx.maxTempLength;
			this.maxMetadataSize = ctx.maxMetadataSize;
			this.maxRecursionLevel = ctx.maxRecursionLevel;
			this.maxArchiveRestarts = ctx.maxArchiveRestarts;
			this.maxArchiveLevels = ctx.maxArchiveLevels;
			this.dontEnterImplicitArchives = ctx.dontEnterImplicitArchives;
			this.maxSplitfileThreads = ctx.maxSplitfileThreads;
			this.maxSplitfileBlockRetries = ctx.maxSplitfileBlockRetries;
			this.maxNonSplitfileRetries = ctx.maxNonSplitfileRetries;
			this.allowSplitfiles = ctx.allowSplitfiles;
			this.followRedirects = ctx.followRedirects;
			this.localRequestOnly = ctx.localRequestOnly;
			this.maxDataBlocksPerSegment = ctx.maxDataBlocksPerSegment;
			this.maxCheckBlocksPerSegment = ctx.maxCheckBlocksPerSegment;
			this.cacheLocalRequests = ctx.cacheLocalRequests;
			this.returnZIPManifests = ctx.returnZIPManifests;
		} else if (maskID == SET_RETURN_ARCHIVES) {
			this.maxOutputLength = ctx.maxOutputLength;
			this.maxMetadataSize = ctx.maxMetadataSize;
			this.maxTempLength = ctx.maxTempLength;
			this.maxRecursionLevel = ctx.maxRecursionLevel;
			this.maxArchiveRestarts = ctx.maxArchiveRestarts;
			this.maxArchiveLevels = ctx.maxArchiveLevels;
			this.dontEnterImplicitArchives = ctx.dontEnterImplicitArchives;
			this.maxSplitfileThreads = ctx.maxSplitfileThreads;
			this.maxSplitfileBlockRetries = ctx.maxSplitfileBlockRetries;
			this.maxNonSplitfileRetries = ctx.maxNonSplitfileRetries;
			this.allowSplitfiles = ctx.allowSplitfiles;
			this.followRedirects = ctx.followRedirects;
			this.localRequestOnly = ctx.localRequestOnly;
			this.maxDataBlocksPerSegment = ctx.maxDataBlocksPerSegment;
			this.maxCheckBlocksPerSegment = ctx.maxCheckBlocksPerSegment;
			this.cacheLocalRequests = ctx.cacheLocalRequests;
			this.returnZIPManifests = true;
		}
		else throw new IllegalArgumentException();
	}

	/** Make public, but just call parent for a field for field copy */
	@Override
	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			// Impossible
			throw new Error(e);
		}
	}

	public void removeFrom(ObjectContainer container) {
		if(hasOwnEventProducer) {
			container.activate(eventProducer, 1);
			eventProducer.removeFrom(container);
		}
		// Storing a BlockSet to the database is not supported, see comments on SimpleBlockSet.objectCanNew().
		// allowedMIMETypes is passed in, whoever passes it in is responsible for deleting it.
		container.delete(this);
	}
	
}
