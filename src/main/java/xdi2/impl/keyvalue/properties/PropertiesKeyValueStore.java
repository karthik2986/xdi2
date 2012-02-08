package xdi2.impl.keyvalue.properties;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.commons.codec.binary.Base64;

import xdi2.impl.keyvalue.AbstractKeyValueStore;
import xdi2.impl.keyvalue.KeyValueStore;

/**
 * This class defines access to a properties file. It is used by the
 * PropertiesGraphFactory class to create graphs stored in properties files.
 * 
 * @author markus
 */
class PropertiesKeyValueStore extends AbstractKeyValueStore implements KeyValueStore {

	private File file;
	private Properties properties;

	PropertiesKeyValueStore(File file) {

		this.file = file;
		this.properties = null;
	}

	public void put(String key, String value) {

		String indexlist;

		try {

			indexlist = this.properties.getProperty(key + "___");
			if (indexlist.trim().equals("")) indexlist = null;
		} catch (Exception ex) {

			indexlist = null;
		}

		String[] indices = indexlist == null ? new String[0] : indexlist.trim().split(" ");
		String newindex = indices.length == 0 ? "0" : Integer.toString((Integer.parseInt(indices[indices.length-1]) + 1));

		this.properties.setProperty(key + "___", (indexlist == null ? "" : indexlist + " ") + newindex);
		this.properties.setProperty(key + "___" + newindex, value);
		this.properties.setProperty(key + "___" + hash(value), newindex);
	}

	@Override
	public String getOne(String key) {

		return(super.getOne(key));
	}

	public Iterator<String> getAll(String key) {

		String indexlist;

		try {

			indexlist = this.properties.getProperty(key + "___");
			if (indexlist.trim().equals("")) indexlist = null;
		} catch (Exception ex) {

			indexlist = null;
		}

		String[] indices = indexlist == null ? new String[0] : indexlist.trim().split(" ");

		List<String> contents = new ArrayList<String> (indices.length);

		for (int i=0; i<indices.length; i++) {

			String content = this.properties.getProperty(key + "___" + indices[i]);
			if (content == null) continue;

			contents.add(content);
		}

		return(contents.iterator());
	}

	@Override
	public boolean contains(String key) {

		return(super.contains(key));
	}

	@Override
	public boolean contains(String key, String value) {

		return(super.contains(key, value));
	}

	@Override
	public void delete(String key) {

		this.properties.setProperty(key + "___", "");
	}

	public void delete(String key, String value) {

		String indexlist;

		try {

			indexlist = this.properties.getProperty(key + "___");
			if (indexlist.trim().equals("")) indexlist = null;
		} catch (Exception ex) {

			indexlist = null;
		}

		String[] indices = indexlist == null ? new String[0] : indexlist.trim().split(" ");

		String index;

		try {

			index = this.properties.getProperty(key + "___" + hash(value));
		} catch(Exception ex) {

			index = null;
		}

		if (index == null) return;

		String newindexlist = "";

		for (int i=0; i<indices.length; i++) {

			if (indices[i].equals(index)) continue;
			newindexlist += " " + indices[i];
		}

		this.properties.setProperty(key + "___", newindexlist);
	}

	public void clear() {

		this.properties.clear();
	}

	public void close() {

		try {

			this.save();
		} catch (Exception ex) {

			throw new RuntimeException("Cannot save properties file: " + ex.getMessage(), ex);
		}

		this.file = null;
		this.properties = null;
	}

	public void beginTransaction() {

		try {

			if (this.properties == null) this.load();
		} catch (Exception ex) {

			throw new RuntimeException("Cannot load properties file: " + ex.getMessage(), ex);
		}
	}

	public void commitTransaction() {

		try {

			this.save();
		} catch (Exception ex) {

			throw new RuntimeException("Cannot save properties file: " + ex.getMessage(), ex);
		}
	}

	public void rollbackTransaction() {

		try {

			this.load();
		} catch (Exception ex) {

			throw new RuntimeException("Cannot load properties file: " + ex.getMessage(), ex);
		}
	}

	public File getFile() {

		return(this.file);
	}

	public Properties getProperties() {

		return(this.properties);
	}

	void load() throws IOException {

		if (! this.file.exists()) this.file.createNewFile();

		this.properties = new Properties();
		this.properties.load(new FileInputStream(this.file));
	}

	void save() throws IOException {

		OutputStream stream = new FileOutputStream(this.file);

		this.properties.store(stream, null);
		stream.close();
	}

	private static String hash(String str) {

		String hash;

		try {

			MessageDigest digest = MessageDigest.getInstance("MD5");
			digest.reset();
			digest.update(str.getBytes());
			hash = new String(Base64.encodeBase64(digest.digest()), "UTF-8");
		} catch (Exception ex) {

			throw new RuntimeException("hash(): " + ex.getMessage(), ex);
		}

		return(hash);
	}
}
