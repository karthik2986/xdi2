package xdi2.tests.core.impl.keyvalue;

import java.io.IOException;
import java.util.Iterator;

import junit.framework.TestCase;
import xdi2.core.impl.keyvalue.KeyValueStore;
import xdi2.core.util.iterators.IteratorCounter;

public abstract class AbstractKeyValueTest extends TestCase {

	protected abstract KeyValueStore getKeyValueStore(String id) throws IOException;

	public void testBasic() throws Exception {

		KeyValueStore keyValueStore = this.getKeyValueStore(this.getClass().getName() + "-keyvalue-1");

		keyValueStore.put("a", "b");
		keyValueStore.put("c", "d");

		assertEquals(keyValueStore.getOne("a"), "b");
		assertEquals(keyValueStore.getOne("c"), "d");
		assertTrue(keyValueStore.contains("a"));
		assertTrue(keyValueStore.contains("c"));
		assertTrue(keyValueStore.contains("a", "b"));
		assertTrue(keyValueStore.contains("c", "d"));
		assertEquals(keyValueStore.count("a"), 1);
		assertEquals(keyValueStore.count("c"), 1);
		assertEquals(keyValueStore.getAll("a").next(), "b");
		assertEquals(keyValueStore.getAll("c").next(), "d");
		assertEquals(new IteratorCounter(keyValueStore.getAll("a")).count(), 1);
		assertEquals(new IteratorCounter(keyValueStore.getAll("c")).count(), 1);

		keyValueStore.delete("a");
		keyValueStore.delete("c", "d");

		assertNull(keyValueStore.getOne("a"));
		assertNull(keyValueStore.getOne("c"));
		assertFalse(keyValueStore.contains("a"));
		assertFalse(keyValueStore.contains("b"));
		assertFalse(keyValueStore.contains("a", "c"));
		assertFalse(keyValueStore.contains("b", "d"));
		assertEquals(keyValueStore.count("a"), 0);
		assertEquals(keyValueStore.count("c"), 0);
		assertFalse(keyValueStore.getAll("a").hasNext());
		assertFalse(keyValueStore.getAll("c").hasNext());
		assertEquals(new IteratorCounter(keyValueStore.getAll("a")).count(), 0);
		assertEquals(new IteratorCounter(keyValueStore.getAll("c")).count(), 0);
	}

	public void testMulti() throws Exception {

		KeyValueStore keyValueStore = this.getKeyValueStore(this.getClass().getName() + "-keyvalue-2");

		String buf;

		keyValueStore.put("a", "b");
		keyValueStore.put("a", "bb");
		keyValueStore.put("a", "bbbb");
		keyValueStore.put("c", "d");
		keyValueStore.put("c", "ddd");

		assertTrue(keyValueStore.contains("a"));
		assertTrue(keyValueStore.contains("c"));
		assertTrue(keyValueStore.contains("a", "b"));
		assertTrue(keyValueStore.contains("a", "bb"));
		assertTrue(keyValueStore.contains("a", "bbbb"));
		assertTrue(keyValueStore.contains("c", "d"));
		assertTrue(keyValueStore.contains("c", "ddd"));
		assertEquals(keyValueStore.count("a"), 3);
		assertEquals(keyValueStore.count("c"), 2);
		buf = ""; for (Iterator<String> i = keyValueStore.getAll("a"); i.hasNext(); ) buf += i.next(); assertEquals(buf, "bbbbbbb");
		buf = ""; for (Iterator<String> i = keyValueStore.getAll("c"); i.hasNext(); ) buf += i.next(); assertEquals(buf, "dddd");
		assertEquals(new IteratorCounter(keyValueStore.getAll("a")).count(), 3);
		assertEquals(new IteratorCounter(keyValueStore.getAll("c")).count(), 2);

		keyValueStore.delete("a", "bb");
		keyValueStore.delete("c");
		keyValueStore.put("c", "x");

		assertEquals(keyValueStore.getOne("c"), "x");
		assertTrue(keyValueStore.contains("a"));
		assertTrue(keyValueStore.contains("c"));
		assertTrue(keyValueStore.contains("a", "b"));
		assertFalse(keyValueStore.contains("a", "bb"));
		assertTrue(keyValueStore.contains("a", "bbbb"));
		assertFalse(keyValueStore.contains("c", "d"));
		assertFalse(keyValueStore.contains("c", "ddd"));
		assertTrue(keyValueStore.contains("c", "x"));
		assertEquals(keyValueStore.count("a"), 2);
		assertEquals(keyValueStore.count("c"), 1);
		buf = ""; for (Iterator<String> i = keyValueStore.getAll("a"); i.hasNext(); ) buf += i.next(); assertEquals(buf, "bbbbb");
		buf = ""; for (Iterator<String> i = keyValueStore.getAll("c"); i.hasNext(); ) buf += i.next(); assertEquals(buf, "x");
		assertEquals(new IteratorCounter(keyValueStore.getAll("a")).count(), 2);
		assertEquals(new IteratorCounter(keyValueStore.getAll("c")).count(), 1);

		keyValueStore.delete("a");
		keyValueStore.delete("c", "x");

		assertNull(keyValueStore.getOne("a"));
		assertNull(keyValueStore.getOne("c"));
		assertFalse(keyValueStore.contains("a"));
		assertFalse(keyValueStore.contains("b"));
		assertFalse(keyValueStore.contains("a", "c"));
		assertFalse(keyValueStore.contains("b", "d"));
		assertEquals(keyValueStore.count("a"), 0);
		assertEquals(keyValueStore.count("c"), 0);
		assertFalse(keyValueStore.getAll("a").hasNext());
		assertFalse(keyValueStore.getAll("c").hasNext());
		assertEquals(new IteratorCounter(keyValueStore.getAll("a")).count(), 0);
		assertEquals(new IteratorCounter(keyValueStore.getAll("c")).count(), 0);
	}

	public void testClear() throws Exception {

		KeyValueStore keyValueStore = this.getKeyValueStore(this.getClass().getName() + "-keyvalue-3");

		keyValueStore.put("a", "b");
		keyValueStore.put("a", "bb");
		keyValueStore.put("a", "bbbb");
		keyValueStore.put("c", "d");
		keyValueStore.put("c", "ddd");

		keyValueStore.clear();

		assertNull(keyValueStore.getOne("a"));
		assertNull(keyValueStore.getOne("c"));
		assertFalse(keyValueStore.contains("a"));
		assertFalse(keyValueStore.contains("b"));
		assertFalse(keyValueStore.contains("a", "c"));
		assertFalse(keyValueStore.contains("b", "d"));
		assertEquals(keyValueStore.count("a"), 0);
		assertEquals(keyValueStore.count("c"), 0);
		assertFalse(keyValueStore.getAll("a").hasNext());
		assertFalse(keyValueStore.getAll("c").hasNext());
		assertEquals(new IteratorCounter(keyValueStore.getAll("a")).count(), 0);
		assertEquals(new IteratorCounter(keyValueStore.getAll("c")).count(), 0);
	}

	public void testReplace() throws Exception {

		KeyValueStore keyValueStore = this.getKeyValueStore(this.getClass().getName() + "-keyvalue-4");

		keyValueStore.put("a", "b");

		assertEquals(keyValueStore.getOne("a"), "b");

		keyValueStore.replace("a", "c");

		assertEquals(keyValueStore.getOne("a"), "c");
	}

	public void testDuplicate() throws Exception {

		KeyValueStore keyValueStore = this.getKeyValueStore(this.getClass().getName() + "-keyvalue-5");

		String buf;

		keyValueStore.put("a", "b");
		keyValueStore.put("a", "c");
		keyValueStore.put("a", "b");

		assertTrue(keyValueStore.contains("a"));
		assertTrue(keyValueStore.contains("a", "b"));
		assertTrue(keyValueStore.contains("a", "c"));
		assertEquals(keyValueStore.count("a"), 2);
		assertEquals(new IteratorCounter(keyValueStore.getAll("a")).count(), 2);
		buf = ""; for (Iterator<String> i = keyValueStore.getAll("a"); i.hasNext(); ) buf += i.next(); assertTrue(buf.equals("bc") || buf.equals("cb"));

		keyValueStore.delete("a", "c");

		assertTrue(keyValueStore.contains("a"));
		assertTrue(keyValueStore.contains("a", "b"));
		assertFalse(keyValueStore.contains("a", "c"));
		assertEquals(keyValueStore.count("a"), 1);
		assertEquals(new IteratorCounter(keyValueStore.getAll("a")).count(), 1);
		buf = ""; for (Iterator<String> i = keyValueStore.getAll("a"); i.hasNext(); ) buf += i.next(); assertEquals(buf, "b");

		keyValueStore.delete("a", "b");

		assertFalse(keyValueStore.contains("a"));
		assertFalse(keyValueStore.contains("a", "b"));
		assertFalse(keyValueStore.contains("a", "c"));
		assertEquals(keyValueStore.count("a"), 0);
		assertEquals(new IteratorCounter(keyValueStore.getAll("a")).count(), 0);
		buf = ""; for (Iterator<String> i = keyValueStore.getAll("a"); i.hasNext(); ) buf += i.next(); assertEquals(buf, "");

		keyValueStore.put("a", "c");

		assertTrue(keyValueStore.contains("a"));
		assertFalse(keyValueStore.contains("a", "b"));
		assertTrue(keyValueStore.contains("a", "c"));
		assertEquals(keyValueStore.count("a"), 1);
		assertEquals(new IteratorCounter(keyValueStore.getAll("a")).count(), 1);
		buf = ""; for (Iterator<String> i = keyValueStore.getAll("a"); i.hasNext(); ) buf += i.next(); assertEquals(buf, "c");
	}
}
