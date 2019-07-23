package br.com.danilopaixao.vehicle.track.queue;

import java.util.Date;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.danilopaixao.vehicle.track.model.VehicleTrack;
 
@Component
public class VehicleTrackQueueSender {
 
    @Autowired
    private RabbitTemplate rabbitTemplate;
 
//    @Autowired
//	private VehicleService vehicleService;
    
    @Autowired
    private Queue queue;
 
    public VehicleTrack sendQueue(final String vin) throws JsonProcessingException {
    	System.out.println("###### VehicleTrackQueueSender#sendQueue#"+vin);
    	
    	ObjectMapper jsonMapper = new ObjectMapper();
    	VehicleTrack vehicleTrack = new VehicleTrack(vin, "", "ON", new Date());
    	 String payload = jsonMapper.writeValueAsString(vehicleTrack);
        rabbitTemplate.convertAndSend(this.queue.getName(), payload);
        return vehicleTrack;
    }
}