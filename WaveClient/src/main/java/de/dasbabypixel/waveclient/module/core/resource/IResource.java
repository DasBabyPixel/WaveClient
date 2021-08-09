package de.dasbabypixel.waveclient.module.core.resource;

import java.io.InputStream;

public interface IResource {

	/**
	 * @return an InputStream for the Resource.
	 */
	InputStream getInputStream();

}
