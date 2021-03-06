package ncadvanced2018.groupeone.parent.controller;

import ncadvanced2018.groupeone.parent.model.entity.Office;
import ncadvanced2018.groupeone.parent.model.entity.impl.RealOffice;
import ncadvanced2018.groupeone.parent.service.OfficeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/office")
public class OfficeController {

    private OfficeService officeService;

    @Autowired
    public OfficeController(OfficeService officeService) {
        this.officeService = officeService;
    }

    @PreAuthorize("hasAnyRole('CLIENT', 'VIP_CLIENT', 'CALL_CENTER_AGENT')")
    @GetMapping
    public ResponseEntity<List<Office>> fetchOfficesActiveAll(){
        List<Office> all = officeService.findAllActive();
        return new ResponseEntity<>(all, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    public ResponseEntity<List<Office>> fetchOfficesAll(){
        List<Office> all = officeService.findAll();
        return new ResponseEntity<>(all, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/sort")
    public ResponseEntity<List<Office>> fetchOfficesAllSorted(@RequestParam String sortedField, @RequestParam boolean asc){
        List<Office> all = officeService.findAll(sortedField, asc);
        return new ResponseEntity<>(all, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<Office> getOfficeById(@PathVariable Long id){
        Office byId = officeService.findById(id);
        return new ResponseEntity<>(byId, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Office> create(@RequestBody RealOffice office){
        Office createdOffice = officeService.create(office);
        return new ResponseEntity<>(createdOffice, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/deactivate")
    public ResponseEntity deactivateOffice(@RequestBody Office office){
        office.setIsActive(false);
        officeService.update(office);
        return new ResponseEntity(HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/activate")
    public ResponseEntity activateOffice(@RequestBody Office office){
        office.setIsActive(true);
        officeService.update(office);
        return new ResponseEntity(HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Office> updateOffice(@RequestBody Office office){
        Office updatedOffice = officeService.update(office);
        return new ResponseEntity<>(updatedOffice, HttpStatus.CREATED);

    }

}
