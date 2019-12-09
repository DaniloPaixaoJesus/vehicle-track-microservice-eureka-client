package br.com.danilopaixao.vehicle.track.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;

import br.com.danilopaixao.vehicle.track.enums.StatusEnum;
import br.com.danilopaixao.vehicle.track.model.Location;
import br.com.danilopaixao.vehicle.track.model.Vehicle;
import br.com.danilopaixao.vehicle.track.model.VehicleList;


@Service
public class VehicleService {
	
	private static final Logger logger = LoggerFactory.getLogger(VehicleService.class);
	
	@Value("${br.com.danilopaixao.service.vehicle.url}")
	private String vehicleRestApiUrl;
	
	@Autowired
	private RestTemplate restTemplate;
	
	@HystrixCommand(fallbackMethod ="getAllVehicleFallBack",
			threadPoolKey = "getAllVehicleThreadPool",
			threadPoolProperties = {
					@HystrixProperty(name = "coreSize", value = "75"),
					@HystrixProperty(name = "maxQueueSize", value = "35")
			},
			commandProperties = {
				@HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "2000"),
				@HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "5"),
				@HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "50"),
				@HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds", value = "5000")
			}
	)
	public VehicleList getAllVehicle() {
		return restTemplate.getForObject(vehicleRestApiUrl+"/vehicles", VehicleList.class);
	}
	public VehicleList getAllOnLineVehicle() {
		return restTemplate.getForObject(vehicleRestApiUrl+"/vehicles", VehicleList.class);
	}
	public VehicleList getAllVehicleFallBack() {
		logger.error("error vehicle rest service ##VehicleService#getAllVehicle({})");
		return null;
	}
	
	@HystrixCommand(fallbackMethod ="getVehicleFallBack",
			threadPoolKey = "getVehicleThreadPool",
			threadPoolProperties = {
					@HystrixProperty(name = "coreSize", value = "75"),
					@HystrixProperty(name = "maxQueueSize", value = "35")
			},
			commandProperties = {
				@HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "2000"),
				@HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "5"),
				@HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "50"),
				@HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds", value = "5000")
			}
	)
	public Vehicle getVehicle(final String vin) {
		if(StringUtils.isEmpty(vin)) {
			return null;
		}
		return restTemplate.getForObject(vehicleRestApiUrl+"/vehicles/"+vin, Vehicle.class);
	}
	public Vehicle getVehicleFallBack(final String vin) {
		logger.error("error vehicle rest service ##VehicleService#getVehicleFallBack({})", vin);
		return new Vehicle(vin, "NOTFOUND", "NOTFOUND", StatusEnum.OFF, "NOTFOUND");
	}
	
	
	@HystrixCommand(fallbackMethod ="updateVehicleStatusFallBack",
			threadPoolKey = "updateVehicleThreadPool",
			threadPoolProperties = {
					@HystrixProperty(name = "coreSize", value = "75"),
					@HystrixProperty(name = "maxQueueSize", value = "35")
			},
			commandProperties = {
				@HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "2000"),
				@HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "5"),
				@HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "50"),
				@HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds", value = "5000")
			}
	)
	public void updateVehicleStatus(final String vin, StatusEnum status, Location location) {
		if(StringUtils.isEmpty(vin)
				|| StringUtils.isEmpty(status)
				|| StringUtils.isEmpty(location)) {
			logger.error("invalid argument ##VehicleService#updateVehicle({}, {}, {})", vin, status, location);
			throw new IllegalArgumentException("location, vin or status null");
		}
		logger.info("##VehicleService#updateVehicle({}, {}, {})", vin, status, location);
		String url = vehicleRestApiUrl+"/vehicles/"+vin+"/status/"+status;
		restTemplate.put(url, location);
	}
	public void updateVehicleStatusFallBack(final String vin, StatusEnum status, Location location) {
		logger.error("error vehicle rest service ##VehicleService#updateVehicleFallBack({}, {}, {})", vin, status, location);
	}
	
}
