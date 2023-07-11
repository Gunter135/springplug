package com.example.springplug.controllers;


import com.example.springplug.models.RequestDTO;
import com.example.springplug.models.ResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@RestController
public class MainController {


    private final Logger logger = LoggerFactory.getLogger(MainController.class);
    ObjectMapper mapper = new ObjectMapper();
    public long start_time = 0L;
    public Map<Character, List<String>> map = new HashMap<>(){{
        put('3', Arrays.asList("50000.00","RU"));
        put('8',Arrays.asList("2000.00","US"));
        put('9',Arrays.asList("1000.00","EU"));
    }};


    @PostMapping(
            value = "/info/postBalances",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> postBalances(@RequestBody RequestDTO requestDTO){
        try{
            String clientId = requestDTO.getClientId();
            char firstDigit = clientId.charAt(0);
            BigDecimal maxLimit;

            String RqUID = requestDTO.getRqUID();
            ResponseDTO responseDTO = new ResponseDTO();

            if (map.get(firstDigit) != null){
                maxLimit = new BigDecimal(map.get(firstDigit).get(0));
                responseDTO.setRqUID(RqUID);
                Random r = new Random();
                responseDTO.setClientId(clientId);
                responseDTO.setAccount(requestDTO.getAccount());
                responseDTO.setCurrency(map.get(firstDigit).get(1));
                responseDTO.setBalance(String.valueOf(r.nextInt(maxLimit.intValue())));
                responseDTO.setMaxLimit(String.valueOf(maxLimit));
            }
            else {
                maxLimit = new BigDecimal("10000.00");
                responseDTO.setRqUID(RqUID);
                Random r = new Random();
                responseDTO.setClientId(clientId);
                responseDTO.setAccount(requestDTO.getAccount());
                responseDTO.setCurrency("Not found");
                responseDTO.setBalance(String.valueOf(r.nextInt(maxLimit.intValue())));
                responseDTO.setMaxLimit(String.valueOf(maxLimit));
            }


            logger.info(" REQUEST "+ mapper.writerWithDefaultPrettyPrinter().writeValueAsString(requestDTO));
            logger.info(" RESPONSE "+ mapper.writerWithDefaultPrettyPrinter().writeValueAsString(responseDTO));

            long pacing = ThreadLocalRandom.current().nextLong(100,500);
            long end_time = System.currentTimeMillis();
            if (end_time - start_time < pacing)
                Thread.sleep(pacing - (end_time - start_time));
//            return new ResponseEntity<>(HttpStatus.OK);
            return new ResponseEntity<>(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(responseDTO),HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
        }


    }
}

/**
 * Запрос:
 * {
 * 	"rqUID": "58dgtf565j8547f64ke7",
 * 	"clientId": "3050000000000000000",
 * 	"account": "30500000000000000001",
 * 	"openDate": "2020-01-01",
 * 	"closeDate": "2025-01-01"
 *        }
 *
 * Ответ:
 * {
 * 	"rqUID": "58dgtf565j8547f64ke7",
 * 	"clientId": "3050000000000000000",
 * 	"account": "30500000000000000001",
 * 	"currency": "RU",
 * 	"balance": "16000.00",
 * 	"maxLimit": "50000.00"
 *    }
 *
 * Задание:
 * Написать эмулятор-заглушку которая на входе будет получать json запрос с номером клиента (clientId) и номером счета(account),
 * rqUID, openDate, closeDate - можно использовать как в примере.
 * в ответ будет выдавать json с информацией по валюте счета(currency), балансу(balance) и максимальному лимиту(maxLimit).
 * balance - любое рандомное число до максимального лимита.
 * Если номер клиента начинается с 8 то валюта счета(currency) доллар - US, максимальный лимит(maxLimit) - 2000.00
 * Пример ответа:
 * {
 * 	"rqUID": "79dgtf565j8158f64gt4",
 * 	"clientId": "8050000000000000000",
 * 	"account": "80500000000000000001",
 * 	"currency": "US",
 * 	"balance": "1400.00",
 * 	"maxLimit": "2000.00"
 *    }
 *
 * Если номер клиента начинается с 9 то валюта счета(currency) евро - EU, максимальный лимит(maxLimit) - 1000.00
 * Пример ответа:
 * {
 * 	"rqUID": "65dpol565j8158f64he5",
 * 	"clientId": "8050000000000000000",
 * 	"account": "80500000000000000001",
 * 	"currency": "EU",
 * 	"balance": "325.00",
 * 	"maxLimit": "1000.00"
 *    }
 *
 * Для отладки заглушки, можно отправлять запрос json через Postman или CURL.
 */


