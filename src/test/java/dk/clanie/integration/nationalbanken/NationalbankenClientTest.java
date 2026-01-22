/*
 * Copyright (C) 2025, Claus Nielsen, clausn999@gmail.com
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package dk.clanie.integration.nationalbanken;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import dk.clanie.integration.nationalbanken.dto.NationalbankensValutakurser;
import dk.clanie.web.WebClientFactory;

class NationalbankenClientTest {

	private NationalbankenClient client;


	@BeforeEach
	void setUp() {
		WebClientFactory webClientFactory = new WebClientFactory(WebClient.builder());
		client = new NationalbankenClient(webClientFactory);
		client.init();
	}


	@Test
	void testParseCurrencyRatesXml() {
		// Given
		String xml = """
				<?xml version="1.0" encoding="utf-8"?>
				<exchangerates type="Valutakurser" author="Danmarks Nationalbank" refcur="DKK" refamt="1">
					<dailyrates id="2026-01-21">
						<currency code="USD" desc="Amerikanske dollar" rate="636,38" />
						<currency code="AUD" desc="Australske dollar" rate="430,82" />
						<currency code="BRL" desc="Brasilianske real" rate="119,01" />
						<currency code="GBP" desc="Britiske pund" rate="854,36" />
						<currency code="CAD" desc="Canadiske dollar" rate="461,37" />
						<currency code="EUR" desc="Euro" rate="747,05" />
						<currency code="PHP" desc="Filippinske peso" rate="10,74" />
						<currency code="HKD" desc="Hongkong dollar" rate="81,62" />
						<currency code="INR" desc="Indiske rupee" rate="6,94" />
						<currency code="IDR" desc="Indonesiske rupiah" rate="0,0376" />
						<currency code="ISK" desc="Islandske kroner" rate="5,11" />
						<currency code="ILS" desc="Israelske shekel" rate="200,63" />
						<currency code="JPY" desc="Japanske yen" rate="4,0331" />
						<currency code="CNY" desc="Kinesiske Yuan renminbi" rate="91,38" />
						<currency code="MYR" desc="Malaysiske ringgit" rate="157,25" />
						<currency code="MXN" desc="Mexicanske peso" rate="36,41" />
						<currency code="NZD" desc="New Zealandske dollar" rate="373,17" />
						<currency code="NOK" desc="Norske kroner" rate="64,20" />
						<currency code="PLN" desc="Polske zloty" rate="176,71" />
						<currency code="RON" desc="Rumænske lei" rate="146,63" />
						<currency code="CHF" desc="Schweiziske franc" rate="806,05" />
						<currency code="XDR" desc="SDR (Beregnet)" rate="871,06" />
						<currency code="SGD" desc="Singapore dollar" rate="496,54" />
						<currency code="SEK" desc="Svenske kroner" rate="70,07" />
						<currency code="ZAR" desc="Sydafrikanske rand" rate="39,02" />
						<currency code="KRW" desc="Sydkoreanske won" rate="0,4345" />
						<currency code="THB" desc="Thailandske baht" rate="20,48" />
						<currency code="CZK" desc="Tjekkiske koruna" rate="30,67" />
						<currency code="TRY" desc="Tyrkiske lira" rate="14,70" />
						<currency code="HUF" desc="Ungarske forint" rate="1,937" />
					</dailyrates>
				</exchangerates>
				""";

		// When
		NationalbankensValutakurser result = client.parseCurrencyRatesXml(xml);

		// Then
		assertThat(result).isNotNull();
		assertThat(result.getDate()).isEqualTo(LocalDate.of(2026, 1, 21));
		assertThat(result.getDkkRates()).hasSize(30);

		// Verify some specific currency rates
		assertThat(result.getDkkRates().get("USD")).isEqualTo(636.38);
		assertThat(result.getDkkRates().get("EUR")).isEqualTo(747.05);
		assertThat(result.getDkkRates().get("GBP")).isEqualTo(854.36);
		assertThat(result.getDkkRates().get("NOK")).isEqualTo(64.20);
		assertThat(result.getDkkRates().get("SEK")).isEqualTo(70.07);
		assertThat(result.getDkkRates().get("JPY")).isEqualTo(4.0331);
		assertThat(result.getDkkRates().get("CHF")).isEqualTo(806.05);
	}


}
