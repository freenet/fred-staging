/*
 * This code is part of Freenet. It is distributed under the GNU General
 * Public License, version 2 (or at your option any later version). See
 * http://www.gnu.org/ for further details of the GPL.
 */



package freenet.support.api;

//~--- non-JDK imports --------------------------------------------------------

import freenet.config.ConfigCallback;

/** Callback (getter/setter) for a string config variable */
public abstract class StringCallback extends ConfigCallback<String> {}
