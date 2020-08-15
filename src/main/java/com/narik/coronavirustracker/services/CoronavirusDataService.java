package com.narik.coronavirustracker.services;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.narik.coronavirustracker.model.CoronavirusStatus;

@Service
public class CoronavirusDataService {

	private static String DATA_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";
	
	private List<CoronavirusStatus> allStats = new ArrayList<>();
	
	 
	public List<CoronavirusStatus> getAllStats() {
		return allStats;
	}
 
	@PostConstruct()
	@Scheduled(cron="* * 1 * * *")
	public void fetchData() {
		
		List<CoronavirusStatus> newStats = new ArrayList<>();
		
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder()
							.uri(URI.create(DATA_URL))
							.build();
		HttpResponse<String> httpResponse = null ; 
		try {
			httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
		} catch (IOException e) {
			
			e.printStackTrace();
		} catch (InterruptedException e) {
			
			e.printStackTrace();
		}
		
		StringReader csvBodyReader = new StringReader(httpResponse.body());
		try {
			Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader()
											.parse(csvBodyReader);
			
			for(CSVRecord record : records) {
				CoronavirusStatus stats  = new CoronavirusStatus();
				stats.setState(record.get("Province/State"));
				stats.setCountry(record.get("Country/Region"));
				
				int todayCases = Integer.parseInt(record.get(record.size()-1)) ;
				int yesterdayCases = Integer.parseInt(record.get(record.size()-2)); 
				 
				stats.setLatestTotalCases(todayCases);
				stats.setDiffFromPrevDay(todayCases - yesterdayCases );
				newStats.add(stats);
			}
			
			
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
		this.allStats = newStats ; 
	}
	
	 
}
