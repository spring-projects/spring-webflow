/*
 * Copyright 2004-2007 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *	  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.webflow.util;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Random;

import junit.framework.TestCase;

/**
 * Test case for {@link Base64}. Based on Base64Test from Jakarta Commons Codec 1.3.
 * 
 * @author Erwin Vervaet
 */
public class Base64Tests extends TestCase {

	private Random random = new Random();

	public Random getRandom() {
		return this.random;
	}

	public void testBase64() {
		assertEquals("SGVsbG8gV29ybGQ=", new Base64().encodeToString("Hello World".getBytes()));
		assertEquals("SGVsbG8gV29ybGQ.", new Base64(true).encodeToString("Hello World".getBytes()));
	}

	public void testDecodePadMarkerIndex2() {
		assertEquals("A", new String(new Base64().decodeFromString("QQ==")));
		assertEquals("A", new String(new Base64(true).decodeFromString("QQ..")));
	}

	public void testDecodePadMarkerIndex3() {
		assertEquals("AA", new String(new Base64().decodeFromString("QUE=")));
		assertEquals("AAA", new String(new Base64().decodeFromString("QUFB")));
		assertEquals("AA", new String(new Base64(true).decodeFromString("QUE.")));
		assertEquals("AAA", new String(new Base64(true).decodeFromString("QUFB")));
	}

	public void testEncodeEmptyString() {
		assertEquals("", new Base64().encodeToString("".getBytes()));
	}

	public void testDecodeEmptyString() {
		assertEquals("", new String(new Base64().decodeFromString("")));
	}

	// encode/decode random arrays from size 0 to size 11
	public void testEncodeDecodeSmall() {
		for (int i = 0; i < 12; i++) {
			byte[] data = new byte[i];
			this.getRandom().nextBytes(data);
			byte[] enc = new Base64().encode(data);
			byte[] data2 = new Base64().decode(enc);
			assertTrue(Arrays.equals(data, data2));
		}
	}

	// encode/decode a large random array
	public void testEncodeDecodeRandom() {
		for (int i = 1; i < 5; i++) {
			byte[] data = new byte[this.getRandom().nextInt(10000) + 1];
			this.getRandom().nextBytes(data);
			byte[] enc = new Base64().encode(data);
			byte[] data2 = new Base64().decode(enc);
			assertTrue(Arrays.equals(data, data2));
		}
	}

	public void testSingletons() {
		assertEquals("AA==", new String(new Base64().encode(new byte[] { (byte) 0 })));
		assertEquals("AQ==", new String(new Base64().encode(new byte[] { (byte) 1 })));
		assertEquals("Ag==", new String(new Base64().encode(new byte[] { (byte) 2 })));
		assertEquals("Aw==", new String(new Base64().encode(new byte[] { (byte) 3 })));
		assertEquals("BA==", new String(new Base64().encode(new byte[] { (byte) 4 })));
		assertEquals("BQ==", new String(new Base64().encode(new byte[] { (byte) 5 })));
		assertEquals("Bg==", new String(new Base64().encode(new byte[] { (byte) 6 })));
		assertEquals("Bw==", new String(new Base64().encode(new byte[] { (byte) 7 })));
		assertEquals("CA==", new String(new Base64().encode(new byte[] { (byte) 8 })));
		assertEquals("CQ==", new String(new Base64().encode(new byte[] { (byte) 9 })));
		assertEquals("Cg==", new String(new Base64().encode(new byte[] { (byte) 10 })));
		assertEquals("Cw==", new String(new Base64().encode(new byte[] { (byte) 11 })));
		assertEquals("DA==", new String(new Base64().encode(new byte[] { (byte) 12 })));
		assertEquals("DQ==", new String(new Base64().encode(new byte[] { (byte) 13 })));
		assertEquals("Dg==", new String(new Base64().encode(new byte[] { (byte) 14 })));
		assertEquals("Dw==", new String(new Base64().encode(new byte[] { (byte) 15 })));
		assertEquals("EA==", new String(new Base64().encode(new byte[] { (byte) 16 })));
		assertEquals("EQ==", new String(new Base64().encode(new byte[] { (byte) 17 })));
		assertEquals("Eg==", new String(new Base64().encode(new byte[] { (byte) 18 })));
		assertEquals("Ew==", new String(new Base64().encode(new byte[] { (byte) 19 })));
		assertEquals("FA==", new String(new Base64().encode(new byte[] { (byte) 20 })));
		assertEquals("FQ==", new String(new Base64().encode(new byte[] { (byte) 21 })));
		assertEquals("Fg==", new String(new Base64().encode(new byte[] { (byte) 22 })));
		assertEquals("Fw==", new String(new Base64().encode(new byte[] { (byte) 23 })));
		assertEquals("GA==", new String(new Base64().encode(new byte[] { (byte) 24 })));
		assertEquals("GQ==", new String(new Base64().encode(new byte[] { (byte) 25 })));
		assertEquals("Gg==", new String(new Base64().encode(new byte[] { (byte) 26 })));
		assertEquals("Gw==", new String(new Base64().encode(new byte[] { (byte) 27 })));
		assertEquals("HA==", new String(new Base64().encode(new byte[] { (byte) 28 })));
		assertEquals("HQ==", new String(new Base64().encode(new byte[] { (byte) 29 })));
		assertEquals("Hg==", new String(new Base64().encode(new byte[] { (byte) 30 })));
		assertEquals("Hw==", new String(new Base64().encode(new byte[] { (byte) 31 })));
		assertEquals("IA==", new String(new Base64().encode(new byte[] { (byte) 32 })));
		assertEquals("IQ==", new String(new Base64().encode(new byte[] { (byte) 33 })));
		assertEquals("Ig==", new String(new Base64().encode(new byte[] { (byte) 34 })));
		assertEquals("Iw==", new String(new Base64().encode(new byte[] { (byte) 35 })));
		assertEquals("JA==", new String(new Base64().encode(new byte[] { (byte) 36 })));
		assertEquals("JQ==", new String(new Base64().encode(new byte[] { (byte) 37 })));
		assertEquals("Jg==", new String(new Base64().encode(new byte[] { (byte) 38 })));
		assertEquals("Jw==", new String(new Base64().encode(new byte[] { (byte) 39 })));
		assertEquals("KA==", new String(new Base64().encode(new byte[] { (byte) 40 })));
		assertEquals("KQ==", new String(new Base64().encode(new byte[] { (byte) 41 })));
		assertEquals("Kg==", new String(new Base64().encode(new byte[] { (byte) 42 })));
		assertEquals("Kw==", new String(new Base64().encode(new byte[] { (byte) 43 })));
		assertEquals("LA==", new String(new Base64().encode(new byte[] { (byte) 44 })));
		assertEquals("LQ==", new String(new Base64().encode(new byte[] { (byte) 45 })));
		assertEquals("Lg==", new String(new Base64().encode(new byte[] { (byte) 46 })));
		assertEquals("Lw==", new String(new Base64().encode(new byte[] { (byte) 47 })));
		assertEquals("MA==", new String(new Base64().encode(new byte[] { (byte) 48 })));
		assertEquals("MQ==", new String(new Base64().encode(new byte[] { (byte) 49 })));
		assertEquals("Mg==", new String(new Base64().encode(new byte[] { (byte) 50 })));
		assertEquals("Mw==", new String(new Base64().encode(new byte[] { (byte) 51 })));
		assertEquals("NA==", new String(new Base64().encode(new byte[] { (byte) 52 })));
		assertEquals("NQ==", new String(new Base64().encode(new byte[] { (byte) 53 })));
		assertEquals("Ng==", new String(new Base64().encode(new byte[] { (byte) 54 })));
		assertEquals("Nw==", new String(new Base64().encode(new byte[] { (byte) 55 })));
		assertEquals("OA==", new String(new Base64().encode(new byte[] { (byte) 56 })));
		assertEquals("OQ==", new String(new Base64().encode(new byte[] { (byte) 57 })));
		assertEquals("Og==", new String(new Base64().encode(new byte[] { (byte) 58 })));
		assertEquals("Ow==", new String(new Base64().encode(new byte[] { (byte) 59 })));
		assertEquals("PA==", new String(new Base64().encode(new byte[] { (byte) 60 })));
		assertEquals("PQ==", new String(new Base64().encode(new byte[] { (byte) 61 })));
		assertEquals("Pg==", new String(new Base64().encode(new byte[] { (byte) 62 })));
		assertEquals("Pw==", new String(new Base64().encode(new byte[] { (byte) 63 })));
		assertEquals("QA==", new String(new Base64().encode(new byte[] { (byte) 64 })));
		assertEquals("QQ==", new String(new Base64().encode(new byte[] { (byte) 65 })));
		assertEquals("Qg==", new String(new Base64().encode(new byte[] { (byte) 66 })));
		assertEquals("Qw==", new String(new Base64().encode(new byte[] { (byte) 67 })));
		assertEquals("RA==", new String(new Base64().encode(new byte[] { (byte) 68 })));
		assertEquals("RQ==", new String(new Base64().encode(new byte[] { (byte) 69 })));
		assertEquals("Rg==", new String(new Base64().encode(new byte[] { (byte) 70 })));
		assertEquals("Rw==", new String(new Base64().encode(new byte[] { (byte) 71 })));
		assertEquals("SA==", new String(new Base64().encode(new byte[] { (byte) 72 })));
		assertEquals("SQ==", new String(new Base64().encode(new byte[] { (byte) 73 })));
		assertEquals("Sg==", new String(new Base64().encode(new byte[] { (byte) 74 })));
		assertEquals("Sw==", new String(new Base64().encode(new byte[] { (byte) 75 })));
		assertEquals("TA==", new String(new Base64().encode(new byte[] { (byte) 76 })));
		assertEquals("TQ==", new String(new Base64().encode(new byte[] { (byte) 77 })));
		assertEquals("Tg==", new String(new Base64().encode(new byte[] { (byte) 78 })));
		assertEquals("Tw==", new String(new Base64().encode(new byte[] { (byte) 79 })));
		assertEquals("UA==", new String(new Base64().encode(new byte[] { (byte) 80 })));
		assertEquals("UQ==", new String(new Base64().encode(new byte[] { (byte) 81 })));
		assertEquals("Ug==", new String(new Base64().encode(new byte[] { (byte) 82 })));
		assertEquals("Uw==", new String(new Base64().encode(new byte[] { (byte) 83 })));
		assertEquals("VA==", new String(new Base64().encode(new byte[] { (byte) 84 })));
		assertEquals("VQ==", new String(new Base64().encode(new byte[] { (byte) 85 })));
		assertEquals("Vg==", new String(new Base64().encode(new byte[] { (byte) 86 })));
		assertEquals("Vw==", new String(new Base64().encode(new byte[] { (byte) 87 })));
		assertEquals("WA==", new String(new Base64().encode(new byte[] { (byte) 88 })));
		assertEquals("WQ==", new String(new Base64().encode(new byte[] { (byte) 89 })));
		assertEquals("Wg==", new String(new Base64().encode(new byte[] { (byte) 90 })));
		assertEquals("Ww==", new String(new Base64().encode(new byte[] { (byte) 91 })));
		assertEquals("XA==", new String(new Base64().encode(new byte[] { (byte) 92 })));
		assertEquals("XQ==", new String(new Base64().encode(new byte[] { (byte) 93 })));
		assertEquals("Xg==", new String(new Base64().encode(new byte[] { (byte) 94 })));
		assertEquals("Xw==", new String(new Base64().encode(new byte[] { (byte) 95 })));
		assertEquals("YA==", new String(new Base64().encode(new byte[] { (byte) 96 })));
		assertEquals("YQ==", new String(new Base64().encode(new byte[] { (byte) 97 })));
		assertEquals("Yg==", new String(new Base64().encode(new byte[] { (byte) 98 })));
		assertEquals("Yw==", new String(new Base64().encode(new byte[] { (byte) 99 })));
		assertEquals("ZA==", new String(new Base64().encode(new byte[] { (byte) 100 })));
		assertEquals("ZQ==", new String(new Base64().encode(new byte[] { (byte) 101 })));
		assertEquals("Zg==", new String(new Base64().encode(new byte[] { (byte) 102 })));
		assertEquals("Zw==", new String(new Base64().encode(new byte[] { (byte) 103 })));
		assertEquals("aA==", new String(new Base64().encode(new byte[] { (byte) 104 })));

		assertEquals("AA..", new String(new Base64(true).encode(new byte[] { (byte) 0 })));
		assertEquals("AQ..", new String(new Base64(true).encode(new byte[] { (byte) 1 })));
		assertEquals("Ag..", new String(new Base64(true).encode(new byte[] { (byte) 2 })));
		assertEquals("Aw..", new String(new Base64(true).encode(new byte[] { (byte) 3 })));
		assertEquals("BA..", new String(new Base64(true).encode(new byte[] { (byte) 4 })));
		assertEquals("BQ..", new String(new Base64(true).encode(new byte[] { (byte) 5 })));
		assertEquals("Bg..", new String(new Base64(true).encode(new byte[] { (byte) 6 })));
		assertEquals("Bw..", new String(new Base64(true).encode(new byte[] { (byte) 7 })));
		assertEquals("CA..", new String(new Base64(true).encode(new byte[] { (byte) 8 })));
		assertEquals("CQ..", new String(new Base64(true).encode(new byte[] { (byte) 9 })));
		assertEquals("Cg..", new String(new Base64(true).encode(new byte[] { (byte) 10 })));
		assertEquals("Cw..", new String(new Base64(true).encode(new byte[] { (byte) 11 })));
		assertEquals("DA..", new String(new Base64(true).encode(new byte[] { (byte) 12 })));
		assertEquals("DQ..", new String(new Base64(true).encode(new byte[] { (byte) 13 })));
		assertEquals("Dg..", new String(new Base64(true).encode(new byte[] { (byte) 14 })));
		assertEquals("Dw..", new String(new Base64(true).encode(new byte[] { (byte) 15 })));
		assertEquals("EA..", new String(new Base64(true).encode(new byte[] { (byte) 16 })));
		assertEquals("EQ..", new String(new Base64(true).encode(new byte[] { (byte) 17 })));
		assertEquals("Eg..", new String(new Base64(true).encode(new byte[] { (byte) 18 })));
		assertEquals("Ew..", new String(new Base64(true).encode(new byte[] { (byte) 19 })));
		assertEquals("FA..", new String(new Base64(true).encode(new byte[] { (byte) 20 })));
		assertEquals("FQ..", new String(new Base64(true).encode(new byte[] { (byte) 21 })));
		assertEquals("Fg..", new String(new Base64(true).encode(new byte[] { (byte) 22 })));
		assertEquals("Fw..", new String(new Base64(true).encode(new byte[] { (byte) 23 })));
		assertEquals("GA..", new String(new Base64(true).encode(new byte[] { (byte) 24 })));
		assertEquals("GQ..", new String(new Base64(true).encode(new byte[] { (byte) 25 })));
		assertEquals("Gg..", new String(new Base64(true).encode(new byte[] { (byte) 26 })));
		assertEquals("Gw..", new String(new Base64(true).encode(new byte[] { (byte) 27 })));
		assertEquals("HA..", new String(new Base64(true).encode(new byte[] { (byte) 28 })));
		assertEquals("HQ..", new String(new Base64(true).encode(new byte[] { (byte) 29 })));
		assertEquals("Hg..", new String(new Base64(true).encode(new byte[] { (byte) 30 })));
		assertEquals("Hw..", new String(new Base64(true).encode(new byte[] { (byte) 31 })));
		assertEquals("IA..", new String(new Base64(true).encode(new byte[] { (byte) 32 })));
		assertEquals("IQ..", new String(new Base64(true).encode(new byte[] { (byte) 33 })));
		assertEquals("Ig..", new String(new Base64(true).encode(new byte[] { (byte) 34 })));
		assertEquals("Iw..", new String(new Base64(true).encode(new byte[] { (byte) 35 })));
		assertEquals("JA..", new String(new Base64(true).encode(new byte[] { (byte) 36 })));
		assertEquals("JQ..", new String(new Base64(true).encode(new byte[] { (byte) 37 })));
		assertEquals("Jg..", new String(new Base64(true).encode(new byte[] { (byte) 38 })));
		assertEquals("Jw..", new String(new Base64(true).encode(new byte[] { (byte) 39 })));
		assertEquals("KA..", new String(new Base64(true).encode(new byte[] { (byte) 40 })));
		assertEquals("KQ..", new String(new Base64(true).encode(new byte[] { (byte) 41 })));
		assertEquals("Kg..", new String(new Base64(true).encode(new byte[] { (byte) 42 })));
		assertEquals("Kw..", new String(new Base64(true).encode(new byte[] { (byte) 43 })));
		assertEquals("LA..", new String(new Base64(true).encode(new byte[] { (byte) 44 })));
		assertEquals("LQ..", new String(new Base64(true).encode(new byte[] { (byte) 45 })));
		assertEquals("Lg..", new String(new Base64(true).encode(new byte[] { (byte) 46 })));
		assertEquals("Lw..", new String(new Base64(true).encode(new byte[] { (byte) 47 })));
		assertEquals("MA..", new String(new Base64(true).encode(new byte[] { (byte) 48 })));
		assertEquals("MQ..", new String(new Base64(true).encode(new byte[] { (byte) 49 })));
		assertEquals("Mg..", new String(new Base64(true).encode(new byte[] { (byte) 50 })));
		assertEquals("Mw..", new String(new Base64(true).encode(new byte[] { (byte) 51 })));
		assertEquals("NA..", new String(new Base64(true).encode(new byte[] { (byte) 52 })));
		assertEquals("NQ..", new String(new Base64(true).encode(new byte[] { (byte) 53 })));
		assertEquals("Ng..", new String(new Base64(true).encode(new byte[] { (byte) 54 })));
		assertEquals("Nw..", new String(new Base64(true).encode(new byte[] { (byte) 55 })));
		assertEquals("OA..", new String(new Base64(true).encode(new byte[] { (byte) 56 })));
		assertEquals("OQ..", new String(new Base64(true).encode(new byte[] { (byte) 57 })));
		assertEquals("Og..", new String(new Base64(true).encode(new byte[] { (byte) 58 })));
		assertEquals("Ow..", new String(new Base64(true).encode(new byte[] { (byte) 59 })));
		assertEquals("PA..", new String(new Base64(true).encode(new byte[] { (byte) 60 })));
		assertEquals("PQ..", new String(new Base64(true).encode(new byte[] { (byte) 61 })));
		assertEquals("Pg..", new String(new Base64(true).encode(new byte[] { (byte) 62 })));
		assertEquals("Pw..", new String(new Base64(true).encode(new byte[] { (byte) 63 })));
		assertEquals("QA..", new String(new Base64(true).encode(new byte[] { (byte) 64 })));
		assertEquals("QQ..", new String(new Base64(true).encode(new byte[] { (byte) 65 })));
		assertEquals("Qg..", new String(new Base64(true).encode(new byte[] { (byte) 66 })));
		assertEquals("Qw..", new String(new Base64(true).encode(new byte[] { (byte) 67 })));
		assertEquals("RA..", new String(new Base64(true).encode(new byte[] { (byte) 68 })));
		assertEquals("RQ..", new String(new Base64(true).encode(new byte[] { (byte) 69 })));
		assertEquals("Rg..", new String(new Base64(true).encode(new byte[] { (byte) 70 })));
		assertEquals("Rw..", new String(new Base64(true).encode(new byte[] { (byte) 71 })));
		assertEquals("SA..", new String(new Base64(true).encode(new byte[] { (byte) 72 })));
		assertEquals("SQ..", new String(new Base64(true).encode(new byte[] { (byte) 73 })));
		assertEquals("Sg..", new String(new Base64(true).encode(new byte[] { (byte) 74 })));
		assertEquals("Sw..", new String(new Base64(true).encode(new byte[] { (byte) 75 })));
		assertEquals("TA..", new String(new Base64(true).encode(new byte[] { (byte) 76 })));
		assertEquals("TQ..", new String(new Base64(true).encode(new byte[] { (byte) 77 })));
		assertEquals("Tg..", new String(new Base64(true).encode(new byte[] { (byte) 78 })));
		assertEquals("Tw..", new String(new Base64(true).encode(new byte[] { (byte) 79 })));
		assertEquals("UA..", new String(new Base64(true).encode(new byte[] { (byte) 80 })));
		assertEquals("UQ..", new String(new Base64(true).encode(new byte[] { (byte) 81 })));
		assertEquals("Ug..", new String(new Base64(true).encode(new byte[] { (byte) 82 })));
		assertEquals("Uw..", new String(new Base64(true).encode(new byte[] { (byte) 83 })));
		assertEquals("VA..", new String(new Base64(true).encode(new byte[] { (byte) 84 })));
		assertEquals("VQ..", new String(new Base64(true).encode(new byte[] { (byte) 85 })));
		assertEquals("Vg..", new String(new Base64(true).encode(new byte[] { (byte) 86 })));
		assertEquals("Vw..", new String(new Base64(true).encode(new byte[] { (byte) 87 })));
		assertEquals("WA..", new String(new Base64(true).encode(new byte[] { (byte) 88 })));
		assertEquals("WQ..", new String(new Base64(true).encode(new byte[] { (byte) 89 })));
		assertEquals("Wg..", new String(new Base64(true).encode(new byte[] { (byte) 90 })));
		assertEquals("Ww..", new String(new Base64(true).encode(new byte[] { (byte) 91 })));
		assertEquals("XA..", new String(new Base64(true).encode(new byte[] { (byte) 92 })));
		assertEquals("XQ..", new String(new Base64(true).encode(new byte[] { (byte) 93 })));
		assertEquals("Xg..", new String(new Base64(true).encode(new byte[] { (byte) 94 })));
		assertEquals("Xw..", new String(new Base64(true).encode(new byte[] { (byte) 95 })));
		assertEquals("YA..", new String(new Base64(true).encode(new byte[] { (byte) 96 })));
		assertEquals("YQ..", new String(new Base64(true).encode(new byte[] { (byte) 97 })));
		assertEquals("Yg..", new String(new Base64(true).encode(new byte[] { (byte) 98 })));
		assertEquals("Yw..", new String(new Base64(true).encode(new byte[] { (byte) 99 })));
		assertEquals("ZA..", new String(new Base64(true).encode(new byte[] { (byte) 100 })));
		assertEquals("ZQ..", new String(new Base64(true).encode(new byte[] { (byte) 101 })));
		assertEquals("Zg..", new String(new Base64(true).encode(new byte[] { (byte) 102 })));
		assertEquals("Zw..", new String(new Base64(true).encode(new byte[] { (byte) 103 })));
		assertEquals("aA..", new String(new Base64(true).encode(new byte[] { (byte) 104 })));
	}

	public void testTriplets() {
		assertEquals("AAAA", new String(new Base64().encode(new byte[] { (byte) 0, (byte) 0, (byte) 0 })));
		assertEquals("AAAB", new String(new Base64().encode(new byte[] { (byte) 0, (byte) 0, (byte) 1 })));
		assertEquals("AAAC", new String(new Base64().encode(new byte[] { (byte) 0, (byte) 0, (byte) 2 })));
		assertEquals("AAAD", new String(new Base64().encode(new byte[] { (byte) 0, (byte) 0, (byte) 3 })));
		assertEquals("AAAE", new String(new Base64().encode(new byte[] { (byte) 0, (byte) 0, (byte) 4 })));
		assertEquals("AAAF", new String(new Base64().encode(new byte[] { (byte) 0, (byte) 0, (byte) 5 })));
		assertEquals("AAAG", new String(new Base64().encode(new byte[] { (byte) 0, (byte) 0, (byte) 6 })));
		assertEquals("AAAH", new String(new Base64().encode(new byte[] { (byte) 0, (byte) 0, (byte) 7 })));
		assertEquals("AAAI", new String(new Base64().encode(new byte[] { (byte) 0, (byte) 0, (byte) 8 })));
		assertEquals("AAAJ", new String(new Base64().encode(new byte[] { (byte) 0, (byte) 0, (byte) 9 })));
		assertEquals("AAAK", new String(new Base64().encode(new byte[] { (byte) 0, (byte) 0, (byte) 10 })));
		assertEquals("AAAL", new String(new Base64().encode(new byte[] { (byte) 0, (byte) 0, (byte) 11 })));
		assertEquals("AAAM", new String(new Base64().encode(new byte[] { (byte) 0, (byte) 0, (byte) 12 })));
		assertEquals("AAAN", new String(new Base64().encode(new byte[] { (byte) 0, (byte) 0, (byte) 13 })));
		assertEquals("AAAO", new String(new Base64().encode(new byte[] { (byte) 0, (byte) 0, (byte) 14 })));
		assertEquals("AAAP", new String(new Base64().encode(new byte[] { (byte) 0, (byte) 0, (byte) 15 })));
		assertEquals("AAAQ", new String(new Base64().encode(new byte[] { (byte) 0, (byte) 0, (byte) 16 })));
		assertEquals("AAAR", new String(new Base64().encode(new byte[] { (byte) 0, (byte) 0, (byte) 17 })));
		assertEquals("AAAS", new String(new Base64().encode(new byte[] { (byte) 0, (byte) 0, (byte) 18 })));
		assertEquals("AAAT", new String(new Base64().encode(new byte[] { (byte) 0, (byte) 0, (byte) 19 })));
		assertEquals("AAAU", new String(new Base64().encode(new byte[] { (byte) 0, (byte) 0, (byte) 20 })));
		assertEquals("AAAV", new String(new Base64().encode(new byte[] { (byte) 0, (byte) 0, (byte) 21 })));
		assertEquals("AAAW", new String(new Base64().encode(new byte[] { (byte) 0, (byte) 0, (byte) 22 })));
		assertEquals("AAAX", new String(new Base64().encode(new byte[] { (byte) 0, (byte) 0, (byte) 23 })));
		assertEquals("AAAY", new String(new Base64().encode(new byte[] { (byte) 0, (byte) 0, (byte) 24 })));
		assertEquals("AAAZ", new String(new Base64().encode(new byte[] { (byte) 0, (byte) 0, (byte) 25 })));
		assertEquals("AAAa", new String(new Base64().encode(new byte[] { (byte) 0, (byte) 0, (byte) 26 })));
		assertEquals("AAAb", new String(new Base64().encode(new byte[] { (byte) 0, (byte) 0, (byte) 27 })));
		assertEquals("AAAc", new String(new Base64().encode(new byte[] { (byte) 0, (byte) 0, (byte) 28 })));
		assertEquals("AAAd", new String(new Base64().encode(new byte[] { (byte) 0, (byte) 0, (byte) 29 })));
		assertEquals("AAAe", new String(new Base64().encode(new byte[] { (byte) 0, (byte) 0, (byte) 30 })));
		assertEquals("AAAf", new String(new Base64().encode(new byte[] { (byte) 0, (byte) 0, (byte) 31 })));
		assertEquals("AAAg", new String(new Base64().encode(new byte[] { (byte) 0, (byte) 0, (byte) 32 })));
		assertEquals("AAAh", new String(new Base64().encode(new byte[] { (byte) 0, (byte) 0, (byte) 33 })));
		assertEquals("AAAi", new String(new Base64().encode(new byte[] { (byte) 0, (byte) 0, (byte) 34 })));
		assertEquals("AAAj", new String(new Base64().encode(new byte[] { (byte) 0, (byte) 0, (byte) 35 })));
		assertEquals("AAAk", new String(new Base64().encode(new byte[] { (byte) 0, (byte) 0, (byte) 36 })));
		assertEquals("AAAl", new String(new Base64().encode(new byte[] { (byte) 0, (byte) 0, (byte) 37 })));
		assertEquals("AAAm", new String(new Base64().encode(new byte[] { (byte) 0, (byte) 0, (byte) 38 })));
		assertEquals("AAAn", new String(new Base64().encode(new byte[] { (byte) 0, (byte) 0, (byte) 39 })));
		assertEquals("AAAo", new String(new Base64().encode(new byte[] { (byte) 0, (byte) 0, (byte) 40 })));
		assertEquals("AAAp", new String(new Base64().encode(new byte[] { (byte) 0, (byte) 0, (byte) 41 })));
		assertEquals("AAAq", new String(new Base64().encode(new byte[] { (byte) 0, (byte) 0, (byte) 42 })));
		assertEquals("AAAr", new String(new Base64().encode(new byte[] { (byte) 0, (byte) 0, (byte) 43 })));
		assertEquals("AAAs", new String(new Base64().encode(new byte[] { (byte) 0, (byte) 0, (byte) 44 })));
		assertEquals("AAAt", new String(new Base64().encode(new byte[] { (byte) 0, (byte) 0, (byte) 45 })));
		assertEquals("AAAu", new String(new Base64().encode(new byte[] { (byte) 0, (byte) 0, (byte) 46 })));
		assertEquals("AAAv", new String(new Base64().encode(new byte[] { (byte) 0, (byte) 0, (byte) 47 })));
		assertEquals("AAAw", new String(new Base64().encode(new byte[] { (byte) 0, (byte) 0, (byte) 48 })));
		assertEquals("AAAx", new String(new Base64().encode(new byte[] { (byte) 0, (byte) 0, (byte) 49 })));
		assertEquals("AAAy", new String(new Base64().encode(new byte[] { (byte) 0, (byte) 0, (byte) 50 })));
		assertEquals("AAAz", new String(new Base64().encode(new byte[] { (byte) 0, (byte) 0, (byte) 51 })));
		assertEquals("AAA0", new String(new Base64().encode(new byte[] { (byte) 0, (byte) 0, (byte) 52 })));
		assertEquals("AAA1", new String(new Base64().encode(new byte[] { (byte) 0, (byte) 0, (byte) 53 })));
		assertEquals("AAA2", new String(new Base64().encode(new byte[] { (byte) 0, (byte) 0, (byte) 54 })));
		assertEquals("AAA3", new String(new Base64().encode(new byte[] { (byte) 0, (byte) 0, (byte) 55 })));
		assertEquals("AAA4", new String(new Base64().encode(new byte[] { (byte) 0, (byte) 0, (byte) 56 })));
		assertEquals("AAA5", new String(new Base64().encode(new byte[] { (byte) 0, (byte) 0, (byte) 57 })));
		assertEquals("AAA6", new String(new Base64().encode(new byte[] { (byte) 0, (byte) 0, (byte) 58 })));
		assertEquals("AAA7", new String(new Base64().encode(new byte[] { (byte) 0, (byte) 0, (byte) 59 })));
		assertEquals("AAA8", new String(new Base64().encode(new byte[] { (byte) 0, (byte) 0, (byte) 60 })));
		assertEquals("AAA9", new String(new Base64().encode(new byte[] { (byte) 0, (byte) 0, (byte) 61 })));
		assertEquals("AAA+", new String(new Base64().encode(new byte[] { (byte) 0, (byte) 0, (byte) 62 })));
		assertEquals("AAA/", new String(new Base64().encode(new byte[] { (byte) 0, (byte) 0, (byte) 63 })));

		assertEquals("AAAA", new String(new Base64(true).encode(new byte[] { (byte) 0, (byte) 0, (byte) 0 })));
		assertEquals("AAAB", new String(new Base64(true).encode(new byte[] { (byte) 0, (byte) 0, (byte) 1 })));
		assertEquals("AAAC", new String(new Base64(true).encode(new byte[] { (byte) 0, (byte) 0, (byte) 2 })));
		assertEquals("AAAD", new String(new Base64(true).encode(new byte[] { (byte) 0, (byte) 0, (byte) 3 })));
		assertEquals("AAAE", new String(new Base64(true).encode(new byte[] { (byte) 0, (byte) 0, (byte) 4 })));
		assertEquals("AAAF", new String(new Base64(true).encode(new byte[] { (byte) 0, (byte) 0, (byte) 5 })));
		assertEquals("AAAG", new String(new Base64(true).encode(new byte[] { (byte) 0, (byte) 0, (byte) 6 })));
		assertEquals("AAAH", new String(new Base64(true).encode(new byte[] { (byte) 0, (byte) 0, (byte) 7 })));
		assertEquals("AAAI", new String(new Base64(true).encode(new byte[] { (byte) 0, (byte) 0, (byte) 8 })));
		assertEquals("AAAJ", new String(new Base64(true).encode(new byte[] { (byte) 0, (byte) 0, (byte) 9 })));
		assertEquals("AAAK", new String(new Base64(true).encode(new byte[] { (byte) 0, (byte) 0, (byte) 10 })));
		assertEquals("AAAL", new String(new Base64(true).encode(new byte[] { (byte) 0, (byte) 0, (byte) 11 })));
		assertEquals("AAAM", new String(new Base64(true).encode(new byte[] { (byte) 0, (byte) 0, (byte) 12 })));
		assertEquals("AAAN", new String(new Base64(true).encode(new byte[] { (byte) 0, (byte) 0, (byte) 13 })));
		assertEquals("AAAO", new String(new Base64(true).encode(new byte[] { (byte) 0, (byte) 0, (byte) 14 })));
		assertEquals("AAAP", new String(new Base64(true).encode(new byte[] { (byte) 0, (byte) 0, (byte) 15 })));
		assertEquals("AAAQ", new String(new Base64(true).encode(new byte[] { (byte) 0, (byte) 0, (byte) 16 })));
		assertEquals("AAAR", new String(new Base64(true).encode(new byte[] { (byte) 0, (byte) 0, (byte) 17 })));
		assertEquals("AAAS", new String(new Base64(true).encode(new byte[] { (byte) 0, (byte) 0, (byte) 18 })));
		assertEquals("AAAT", new String(new Base64(true).encode(new byte[] { (byte) 0, (byte) 0, (byte) 19 })));
		assertEquals("AAAU", new String(new Base64(true).encode(new byte[] { (byte) 0, (byte) 0, (byte) 20 })));
		assertEquals("AAAV", new String(new Base64(true).encode(new byte[] { (byte) 0, (byte) 0, (byte) 21 })));
		assertEquals("AAAW", new String(new Base64(true).encode(new byte[] { (byte) 0, (byte) 0, (byte) 22 })));
		assertEquals("AAAX", new String(new Base64(true).encode(new byte[] { (byte) 0, (byte) 0, (byte) 23 })));
		assertEquals("AAAY", new String(new Base64(true).encode(new byte[] { (byte) 0, (byte) 0, (byte) 24 })));
		assertEquals("AAAZ", new String(new Base64(true).encode(new byte[] { (byte) 0, (byte) 0, (byte) 25 })));
		assertEquals("AAAa", new String(new Base64(true).encode(new byte[] { (byte) 0, (byte) 0, (byte) 26 })));
		assertEquals("AAAb", new String(new Base64(true).encode(new byte[] { (byte) 0, (byte) 0, (byte) 27 })));
		assertEquals("AAAc", new String(new Base64(true).encode(new byte[] { (byte) 0, (byte) 0, (byte) 28 })));
		assertEquals("AAAd", new String(new Base64(true).encode(new byte[] { (byte) 0, (byte) 0, (byte) 29 })));
		assertEquals("AAAe", new String(new Base64(true).encode(new byte[] { (byte) 0, (byte) 0, (byte) 30 })));
		assertEquals("AAAf", new String(new Base64(true).encode(new byte[] { (byte) 0, (byte) 0, (byte) 31 })));
		assertEquals("AAAg", new String(new Base64(true).encode(new byte[] { (byte) 0, (byte) 0, (byte) 32 })));
		assertEquals("AAAh", new String(new Base64(true).encode(new byte[] { (byte) 0, (byte) 0, (byte) 33 })));
		assertEquals("AAAi", new String(new Base64(true).encode(new byte[] { (byte) 0, (byte) 0, (byte) 34 })));
		assertEquals("AAAj", new String(new Base64(true).encode(new byte[] { (byte) 0, (byte) 0, (byte) 35 })));
		assertEquals("AAAk", new String(new Base64(true).encode(new byte[] { (byte) 0, (byte) 0, (byte) 36 })));
		assertEquals("AAAl", new String(new Base64(true).encode(new byte[] { (byte) 0, (byte) 0, (byte) 37 })));
		assertEquals("AAAm", new String(new Base64(true).encode(new byte[] { (byte) 0, (byte) 0, (byte) 38 })));
		assertEquals("AAAn", new String(new Base64(true).encode(new byte[] { (byte) 0, (byte) 0, (byte) 39 })));
		assertEquals("AAAo", new String(new Base64(true).encode(new byte[] { (byte) 0, (byte) 0, (byte) 40 })));
		assertEquals("AAAp", new String(new Base64(true).encode(new byte[] { (byte) 0, (byte) 0, (byte) 41 })));
		assertEquals("AAAq", new String(new Base64(true).encode(new byte[] { (byte) 0, (byte) 0, (byte) 42 })));
		assertEquals("AAAr", new String(new Base64(true).encode(new byte[] { (byte) 0, (byte) 0, (byte) 43 })));
		assertEquals("AAAs", new String(new Base64(true).encode(new byte[] { (byte) 0, (byte) 0, (byte) 44 })));
		assertEquals("AAAt", new String(new Base64(true).encode(new byte[] { (byte) 0, (byte) 0, (byte) 45 })));
		assertEquals("AAAu", new String(new Base64(true).encode(new byte[] { (byte) 0, (byte) 0, (byte) 46 })));
		assertEquals("AAAv", new String(new Base64(true).encode(new byte[] { (byte) 0, (byte) 0, (byte) 47 })));
		assertEquals("AAAw", new String(new Base64(true).encode(new byte[] { (byte) 0, (byte) 0, (byte) 48 })));
		assertEquals("AAAx", new String(new Base64(true).encode(new byte[] { (byte) 0, (byte) 0, (byte) 49 })));
		assertEquals("AAAy", new String(new Base64(true).encode(new byte[] { (byte) 0, (byte) 0, (byte) 50 })));
		assertEquals("AAAz", new String(new Base64(true).encode(new byte[] { (byte) 0, (byte) 0, (byte) 51 })));
		assertEquals("AAA0", new String(new Base64(true).encode(new byte[] { (byte) 0, (byte) 0, (byte) 52 })));
		assertEquals("AAA1", new String(new Base64(true).encode(new byte[] { (byte) 0, (byte) 0, (byte) 53 })));
		assertEquals("AAA2", new String(new Base64(true).encode(new byte[] { (byte) 0, (byte) 0, (byte) 54 })));
		assertEquals("AAA3", new String(new Base64(true).encode(new byte[] { (byte) 0, (byte) 0, (byte) 55 })));
		assertEquals("AAA4", new String(new Base64(true).encode(new byte[] { (byte) 0, (byte) 0, (byte) 56 })));
		assertEquals("AAA5", new String(new Base64(true).encode(new byte[] { (byte) 0, (byte) 0, (byte) 57 })));
		assertEquals("AAA6", new String(new Base64(true).encode(new byte[] { (byte) 0, (byte) 0, (byte) 58 })));
		assertEquals("AAA7", new String(new Base64(true).encode(new byte[] { (byte) 0, (byte) 0, (byte) 59 })));
		assertEquals("AAA8", new String(new Base64(true).encode(new byte[] { (byte) 0, (byte) 0, (byte) 60 })));
		assertEquals("AAA9", new String(new Base64(true).encode(new byte[] { (byte) 0, (byte) 0, (byte) 61 })));
		assertEquals("AAA-", new String(new Base64(true).encode(new byte[] { (byte) 0, (byte) 0, (byte) 62 })));
		assertEquals("AAA_", new String(new Base64(true).encode(new byte[] { (byte) 0, (byte) 0, (byte) 63 })));
	}

	public void testKnownEncodings() {
		assertEquals("VGhlIHF1aWNrIGJyb3duIGZveCBqdW1wZWQgb3ZlciB0aGUgbGF6eSBkb2dzLg==", new Base64()
				.encodeToString("The quick brown fox jumped over the lazy dogs.".getBytes()));
		assertEquals("SXQgd2FzIHRoZSBiZXN0IG9mIHRpbWVzLCBpdCB3YXMgdGhlIHdvcnN0IG9mIHRpbWVzLg==", new Base64()
				.encodeToString("It was the best of times, it was the worst of times.".getBytes()));
		assertEquals("aHR0cDovL2pha2FydGEuYXBhY2hlLm9yZy9jb21tbW9ucw==", new Base64()
				.encodeToString("http://jakarta.apache.org/commmons".getBytes()));
		assertEquals("QWFCYkNjRGRFZUZmR2dIaElpSmpLa0xsTW1Obk9vUHBRcVJyU3NUdFV1VnZXd1h4WXlaeg==", new Base64()
				.encodeToString("AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz".getBytes()));
		assertEquals("eyAwLCAxLCAyLCAzLCA0LCA1LCA2LCA3LCA4LCA5IH0=", new Base64()
				.encodeToString("{ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 }".getBytes()));
		assertEquals("eHl6enkh", new String(new Base64().encode("xyzzy!".getBytes())));

		assertEquals("VGhlIHF1aWNrIGJyb3duIGZveCBqdW1wZWQgb3ZlciB0aGUgbGF6eSBkb2dzLg..", new Base64(true)
				.encodeToString("The quick brown fox jumped over the lazy dogs.".getBytes()));
		assertEquals("SXQgd2FzIHRoZSBiZXN0IG9mIHRpbWVzLCBpdCB3YXMgdGhlIHdvcnN0IG9mIHRpbWVzLg..", new Base64(true)
				.encodeToString("It was the best of times, it was the worst of times.".getBytes()));
		assertEquals("aHR0cDovL2pha2FydGEuYXBhY2hlLm9yZy9jb21tbW9ucw..", new Base64(true)
				.encodeToString("http://jakarta.apache.org/commmons".getBytes()));
		assertEquals("QWFCYkNjRGRFZUZmR2dIaElpSmpLa0xsTW1Obk9vUHBRcVJyU3NUdFV1VnZXd1h4WXlaeg..", new Base64(true)
				.encodeToString("AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz".getBytes()));
		assertEquals("eyAwLCAxLCAyLCAzLCA0LCA1LCA2LCA3LCA4LCA5IH0.", new Base64(true)
				.encodeToString("{ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 }".getBytes()));
		assertEquals("eHl6enkh", new String(new Base64(true).encode("xyzzy!".getBytes())));
	}

	public void testKnownDecodings() {
		assertEquals("The quick brown fox jumped over the lazy dogs.", new String(new Base64()
				.decodeFromString("VGhlIHF1aWNrIGJyb3duIGZveCBqdW1wZWQgb3ZlciB0aGUgbGF6eSBkb2dzLg==")));
		assertEquals("It was the best of times, it was the worst of times.", new String(new Base64()
				.decodeFromString("SXQgd2FzIHRoZSBiZXN0IG9mIHRpbWVzLCBpdCB3YXMgdGhlIHdvcnN0IG9mIHRpbWVzLg==")));
		assertEquals("http://jakarta.apache.org/commmons", new String(new Base64()
				.decodeFromString("aHR0cDovL2pha2FydGEuYXBhY2hlLm9yZy9jb21tbW9ucw==")));
		assertEquals("AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz", new String(new Base64()
				.decodeFromString("QWFCYkNjRGRFZUZmR2dIaElpSmpLa0xsTW1Obk9vUHBRcVJyU3NUdFV1VnZXd1h4WXlaeg==")));
		assertEquals("{ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 }", new String(new Base64()
				.decodeFromString("eyAwLCAxLCAyLCAzLCA0LCA1LCA2LCA3LCA4LCA5IH0=")));
		assertEquals("xyzzy!", new String(new Base64().decodeFromString("eHl6enkh")));

		assertEquals("The quick brown fox jumped over the lazy dogs.", new String(new Base64(true)
				.decodeFromString("VGhlIHF1aWNrIGJyb3duIGZveCBqdW1wZWQgb3ZlciB0aGUgbGF6eSBkb2dzLg..")));
		assertEquals("It was the best of times, it was the worst of times.", new String(new Base64(true)
				.decodeFromString("SXQgd2FzIHRoZSBiZXN0IG9mIHRpbWVzLCBpdCB3YXMgdGhlIHdvcnN0IG9mIHRpbWVzLg..")));
		assertEquals("http://jakarta.apache.org/commmons", new String(new Base64(true)
				.decodeFromString("aHR0cDovL2pha2FydGEuYXBhY2hlLm9yZy9jb21tbW9ucw..")));
		assertEquals("AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz", new String(new Base64(true)
				.decodeFromString("QWFCYkNjRGRFZUZmR2dIaElpSmpLa0xsTW1Obk9vUHBRcVJyU3NUdFV1VnZXd1h4WXlaeg..")));
		assertEquals("{ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 }", new String(new Base64(true)
				.decodeFromString("eyAwLCAxLCAyLCAzLCA0LCA1LCA2LCA3LCA4LCA5IH0.")));
		assertEquals("xyzzy!", new String(new Base64(true).decodeFromString("eHl6enkh")));
	}

	public void testNonBase64Test() throws Exception {
		byte[] bArray = { '%' };

		try {
			byte[] result = new Base64().decode(bArray);
			assertTrue("The result should be empty as the test encoded content did "
					+ "not contain any valid base 64 characters", result.length == 0);
		} catch (Exception e) {
			fail("Exception was thrown when trying to decode "
					+ "invalid base64 encoded data - RFC 2045 requires that all "
					+ "non base64 character be discarded, an exception should not" + " have been thrown");
		}
	}

	public void testIgnoringNonBase64InDecode() throws Exception {
		assertEquals(
				"The quick brown fox jumped over the lazy dogs.",
				new String(
						new Base64()
								.decodeFromString("VGhlIH@$#$@%F1aWN@#@#@@rIGJyb3duIGZve\n\r\t%#%#%#%CBqd##$#$W1wZWQgb3ZlciB0aGUgbGF6eSBkb2dzLg==")));

		assertEquals(
				"The quick brown fox jumped over the lazy dogs.",
				new String(
						new Base64(true)
								.decodeFromString("VGhlIH@$#$@%F1aWN@#@#@@rIGJyb3duIGZve\n\r\t%#%#%#%CBqd##$#$W1wZWQgb3ZlciB0aGUgbGF6eSBkb2dzLg..")));
	}

	public void testDecodeWithWhitespace() throws Exception {
		String orig = "I am a late night coder.";

		byte[] encodedArray = new Base64().encode(orig.getBytes());

		StringBuffer intermediate = new StringBuffer(new String(encodedArray));
		intermediate.insert(2, ' ');
		intermediate.insert(5, '\t');
		intermediate.insert(10, '\r');
		intermediate.insert(15, '\n');

		byte[] encodedWithWhitespace = intermediate.toString().getBytes();
		byte[] decodedWithWhitespace = new Base64().decode(encodedWithWhitespace);

		String dest = new String(decodedWithWhitespace);

		assertEquals(orig, dest);
	}

	public void testEncodePaddingChar() {
		assertEquals("=", new String(new Base64().decode(new Base64().encode("=".getBytes()))));
		assertEquals(".", new String(new Base64().decode(new Base64().encode(".".getBytes()))));
		assertEquals("=", new String(new Base64(true).decode(new Base64(true).encode("=".getBytes()))));
		assertEquals(".", new String(new Base64(true).decode(new Base64(true).encode(".".getBytes()))));
	}

	public void testUrlSafety() throws Exception {
		byte[] bytes = new byte[256];
		for (int i = 0; i < 256; i++) {
			bytes[i] = (byte) i;
		}
		byte[] encoded = new Base64(true).encode(bytes);
		String encodedString = new String(encoded, "UTF-8");
		String urlEncoded = URLEncoder.encode(encodedString, "UTF-8");
		assertEquals(encodedString, urlEncoded);
		String urlDecoded = URLDecoder.decode(urlEncoded, "UTF-8");
		assertEquals(encodedString, urlDecoded);
		byte[] decoded = new Base64(true).decode(urlDecoded.getBytes("UTF-8"));
		assertEquals(bytes.length, decoded.length);
		for (int i = 0; i < 256; i++) {
			assertEquals(bytes[i], decoded[i]);
		}
	}
}