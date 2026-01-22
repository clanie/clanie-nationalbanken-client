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

import static com.google.common.collect.Streams.stream;
import static java.util.stream.Collectors.toMap;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.reactive.function.client.WebClient;

import dk.clanie.integration.nationalbanken.dto.NationalbankensValutakurser;
import dk.clanie.web.WebClientFactory;
import dk.clanie.web.exception.InternalServerErrorException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import tools.jackson.databind.JsonNode;
import tools.jackson.dataformat.xml.XmlMapper;

@RequiredArgsConstructor
public class NationalbankenClient {


	@Value("${nationalbanken.url}")
	private String url;

	@Value("#{new Boolean('${nationalbanken.wiretap}')}")
	private boolean wiretap;

	private final WebClientFactory webClientFactory;

	private WebClient wc;
	private XmlMapper xmlMapper;


	@PostConstruct
	public void init() {
		wc = webClientFactory.newWebClient(url, wiretap);
		xmlMapper = new XmlMapper();
	}


	public NationalbankensValutakurser getCurrencyRates() {
		String doc = wc.get()
				.uri("/CurrencyRatesXML?lang=da")
				.retrieve()
				.bodyToMono(String.class)
				.block();
		return parseCurrencyRatesXml(doc);
	}


	/**
	 * Parses the XML response from Nationalbanken's currency rates API.
	 * 
	 * @param xmlDoc the XML document as a string
	 * @return parsed currency rates
	 */
	NationalbankensValutakurser parseCurrencyRatesXml(String xmlDoc) {
		JsonNode tree;
		tree = xmlMapper.readTree(xmlDoc);
		String refCurrency = tree.get("refcur").asString();
		if (!"DKK".equals(refCurrency)) {
			throw new InternalServerErrorException("Expected Nationalbankens valutakurser to be relative to DKK, but refcur is " + refCurrency + ".");
		}
		JsonNode dailyRates = tree.get("dailyrates");
		LocalDate date = LocalDate.parse(dailyRates.get("id").asString());
		Map<String, Double> currencyRates = stream(dailyRates.get("currency").iterator())
				.filter(element -> !element.get("rate").asString().equals("-"))
				.collect(toMap(
						element -> element.get("code").asString(), // Currency code
						element -> new BigDecimal(element.get("rate").asString().replace(",", ".")).doubleValue()));
		return new NationalbankensValutakurser(date, currencyRates);
	}


}
