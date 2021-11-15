package advent.year2015.day12;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class JsonDocumentTest {

	@Test
	public void arrayToSix() {
		JsonDocument document = new JsonDocument("[1, 2, 3]");
		assertEquals(6, document.sumAllNumbers());
	}

	@Test
	public void mapToSix() {
		JsonDocument document = new JsonDocument("{\"a\":2,\"b\":4}");
		assertEquals(6, document.sumAllNumbers());
	}

	@Test
	public void nestedArrayToThree() {
		JsonDocument document = new JsonDocument("[[[3]]]");
		assertEquals(3, document.sumAllNumbers());
	}

	@Test
	public void nestedMapToThree() {
		JsonDocument document = new JsonDocument("{\"a\":{\"b\":4},\"c\":-1}");
		assertEquals(3, document.sumAllNumbers());
	}

	@Test
	public void nestedArrayToZero() {
		JsonDocument document = new JsonDocument("{\"a\":[-1,1]}");
		assertEquals(0, document.sumAllNumbers());
	}

	@Test
	public void nestedMapToZero() {
		JsonDocument document = new JsonDocument("[-1,{\"a\":1}]");
		assertEquals(0, document.sumAllNumbers());
	}

	@Test
	public void emptyArray() {
		JsonDocument document = new JsonDocument("[]");
		assertEquals(0, document.sumAllNumbers());
	}

	@Test
	public void emptyMap() {
		JsonDocument document = new JsonDocument("{}");
		assertEquals(0, document.sumAllNumbers());
	}

	@Test
	public void arrayToSixNonRed() {
		JsonDocument document = new JsonDocument("[1, 2, 3]");
		assertEquals(6, document.sumAllNonRedNumbers());
	}

	@Test
	public void middleObjectNonRed() {
		JsonDocument document = new JsonDocument("[1,{\"c\":\"red\",\"b\":2},3]");
		assertEquals(4, document.sumAllNonRedNumbers());
	}

	@Test
	public void entireStructureNonRed() {
		JsonDocument document = new JsonDocument("{\"d\":\"red\",\"e\":[1,2,3,4],\"f\":5}");
		assertEquals(0, document.sumAllNonRedNumbers());
	}

	@Test
	public void inArrayNonRed() {
		JsonDocument document = new JsonDocument("[1,\"red\",5]");
		assertEquals(6, document.sumAllNonRedNumbers());
	}

	@Test
	public void arrayInObjectNonRed() {
		JsonDocument document = new JsonDocument("{\"a\":[1,\"red\",5]}");
		assertEquals(6, document.sumAllNonRedNumbers());
	}

}
