import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.List;

@RestController
@RequestMapping("/pet")
public interface PetApi {

    @PostMapping("")
    ResponseEntity<Pet> addPet(@RequestBody Pet body);

    @PutMapping("")
    ResponseEntity<Pet> updatePet(@RequestBody Pet body);

    @PostMapping("/{petId}")
    ResponseEntity<Pet> updatePetWithForm(@PathVariable("petId") Integer petId, @RequestParam("name") String name, @RequestParam("status") String status);

    @GetMapping("/{petId}")
    ResponseEntity<Pet> getById(@PathVariable("petId") Integer petId);

    @GetMapping("/findByStatus")
    ResponseEntity<List<Pet>> findByStatus(@RequestParam("status") String status);

    @DeleteMapping("/{petId}")
    ResponseEntity<Void> deletePet(@PathVariable("petId") Integer petId);

}
