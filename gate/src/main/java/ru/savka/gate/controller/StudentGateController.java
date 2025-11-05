package ru.savka.gate.controller;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.savka.gate.api.StudentGateApi;
import ru.savka.gate.client.api.StudentDataApi;
import ru.savka.gate.client.model.StudentDataCreateRequest;
import ru.savka.gate.client.model.StudentDataResponse;
import ru.savka.gate.model.StudentGateCreateRequest;
import ru.savka.gate.model.StudentGateResponse;

@RestController
@RequiredArgsConstructor
public class StudentGateController implements StudentGateApi {

    private final StudentDataApi studentsFeignClient;

    /**
     * Проксирует создание студента во внутренний DATA-SERVICE.
     * Получает запрос от клиента, преобразует модель и перенаправляет.
     */
    @Override
    public ResponseEntity<StudentGateResponse> createStudent(StudentGateCreateRequest request) {
        StudentDataCreateRequest dataRequest = new StudentDataCreateRequest();
        dataRequest.setFullName(request.getFullName());
        dataRequest.setPassport(request.getPassport());

        StudentDataResponse dataResponse = studentsFeignClient.createStudentDataInData(dataRequest);

        StudentGateResponse gateResponse = new StudentGateResponse();
        gateResponse.setId(dataResponse.getId());
        gateResponse.setFullName(dataResponse.getFullName());
        gateResponse.setPassport(dataResponse.getPassport());

        return ResponseEntity.status(201).body(gateResponse);
    }

    @Override
    public ResponseEntity<StudentGateResponse> getStudentById(Long id) {
        try {
            StudentDataResponse studentData = studentsFeignClient.getStudentDataByIdFromData(id);
            StudentGateResponse response = new StudentGateResponse();
            response.setId(studentData.getId());
            response.setFullName(studentData.getFullName());
            response.setPassport(studentData.getPassport());
            return ResponseEntity.ok(response);
        } catch (FeignException e) {
            return ResponseEntity.status(e.status()).build();
        }
    }
}
