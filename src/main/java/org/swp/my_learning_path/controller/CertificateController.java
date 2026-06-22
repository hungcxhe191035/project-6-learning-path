package org.swp.my_learning_path.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.swp.my_learning_path.entity.Certificate;
import org.swp.my_learning_path.service.CertificateService;

@Controller
@RequestMapping("/certificates")
@RequiredArgsConstructor
public class CertificateController {

    private final CertificateService certificateService;

    @GetMapping("/{id}")
    public String viewCertificate(
            @PathVariable Long id,
            Model model) {

        Certificate certificate =
                certificateService.findById(id);

        model.addAttribute("certificate", certificate);

        return "pages/certificate-view";
    }
}