package com.example.Inmopro.v1.Controller.Request;

import com.example.Inmopro.v1.Dto.Request.RequestCancel;
import com.example.Inmopro.v1.Dto.Request.RequestRequest;
import com.example.Inmopro.v1.Model.Request.FollowUpRequest;
import com.example.Inmopro.v1.Repository.FollowUpRequestRepository;
import com.example.Inmopro.v1.Service.Request.RequestService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/request")
@RequiredArgsConstructor
public class RequestController {

    private final RequestService requestService;

    @GetMapping("followuprequests")
    public ResponseEntity<List<FollowUpRequest>> getAllFollowUpRequests() {
        List<FollowUpRequest> followUpRequests = requestService.getFollowUpRequest();
        return ResponseEntity.ok(followUpRequests);
    }

    @GetMapping("/followuprequests/{statusName}")
    public ResponseEntity<List<Object[]>> getFollowUpRequestsByStatusName(@PathVariable String statusName) {
        List<Object[]> followUpRequests = requestService.getFollowUpRequestsByStatusName(statusName);
        if (followUpRequests.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(followUpRequests);
    }

    @PostMapping("create")
    public ResponseEntity<RequestResponse> create(@RequestBody RequestRequest request, HttpServletRequest httpRequest) throws IOException, MessagingException {
        return ResponseEntity.ok(requestService.create(request, httpRequest));
    }

    @PostMapping("cancel")
    public ResponseEntity<RequestResponse> cancel(@RequestBody RequestCancel request) {
        return ResponseEntity.ok(requestService.cancel(request));
    }

}
