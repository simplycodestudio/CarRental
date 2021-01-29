package org.carRental;

import org.carRental.dto.TransferData;
import org.carRental.dto.UserContext;
import org.carRental.dto.UserData;
import org.carRental.model.Konta;
import org.carRental.model.Osoby;
import org.carRental.model.Pojazdy;
import org.carRental.model.Rents;
import org.carRental.repository.KontaRepository;
import org.carRental.repository.PojazdRepository;
import org.carRental.repository.RentsRepository;
import org.carRental.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@org.springframework.stereotype.Controller
public class Controller {


    private PojazdRepository pojazdRepository;
    private KontaRepository kontaRepository;
    private RentsRepository rentsRepository;
    private UserRepository userRepository;
    private UserContext userContext;
    private TransferData transferData;

    @Autowired
    public Controller(PojazdRepository pojazdRepository, UserContext userContext, KontaRepository kontaRepository, TransferData transferData, RentsRepository rentsRepository, UserRepository userRepository) {
        this.pojazdRepository = pojazdRepository;
        this.userContext = userContext;
        this.kontaRepository = kontaRepository;
        this.transferData = transferData;
        this.rentsRepository = rentsRepository;
        this.userRepository = userRepository;
    }

    @GetMapping(value = "/")
    public String start(Model model) {

        model.addAttribute("userContext",userContext);
        return "index";

    }

    @GetMapping(value = "pojazdy")
    public String pokazPojazdy(Model model) {
        String role = "";
        model.addAttribute("pojazdy", pojazdRepository.findAll());
        model.addAttribute("wynajmy", rentsRepository.findAll().stream().map(Rents::getPojazd).collect(Collectors.toList()));
        if (userContext.getUserId() != null) {
            role = kontaRepository.getOne(userContext.getUserId()).getRola();
        }
        model.addAttribute("rola", role);


        return "dostepnePojazdy";
    }

    @GetMapping(value = "wynajmij/{id}")
    public String wynajmijPojazd(@PathVariable("id") long id, Model model) {
        model.addAttribute("userData", new UserData());
        transferData.setIdPojazdu(id);
        if (userContext.getPoswiadczeniaUzytkownika().isEmpty()) {
            return "zaloguj";
        }
        model.addAttribute("transferData", transferData);
        model.addAttribute("pojazdDoWynajecia", pojazdRepository.getOne(transferData.getIdPojazdu()));
        return "formularzNajmow";
    }

    @RequestMapping(value = "zaloguj", produces = "application/json",
            method = {RequestMethod.GET, RequestMethod.PUT, RequestMethod.POST})
    public String zaloguj(
            @Valid UserData userData,
            Model model,
            RedirectAttributes redirectAttributes) {


        String encodedUserDetails = AppUtils.encodeToBase64(userData);
        List<Konta> konta = kontaRepository.findByCredentials(encodedUserDetails);
        if (konta.size() == 0) {
            return "zaloguj";
        } else {
            userContext.setPoswiadczeniaUzytkownika(konta.get(0).getDaneLogowania());
            userContext.setUserId(konta.get(0).getOsoba().getId());
        }

//        if (kontaRepository.findByCredentials(encodedString))
        Pojazdy one = pojazdRepository.getOne(transferData.getIdPojazdu());

        model.addAttribute("pojazdDoWynajecia", one);
        transferData.setIdPojazdu(one.getId());
        model.addAttribute("transferData", transferData);
        return "redirect:/";
    }


    @RequestMapping(value = "zarejestruj", produces = "application/json",
            method = {RequestMethod.GET, RequestMethod.PUT, RequestMethod.POST})
    public String zarejestruj(
            @Valid UserData userData,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (userData.getLogin()!=null){
            userRepository.save(new Osoby(userData.getImie(), userData.getNazwisko(),userData.getNumerDowodu(),userData.getNumerTelefonu()));
            Osoby byNumerDowodu = userRepository.findByNumerDowodu(userData.getNumerDowodu());
            kontaRepository.save(new Konta(byNumerDowodu, AppUtils.encodeToBase64(userData),"CLIENT") );
            return "redirect:/";
        }
        model.addAttribute("userData", new UserData());

        return "zarejestruj";
    }

    @RequestMapping(value = "potwierdz/{id}", produces = "application/json",
            method = {RequestMethod.GET, RequestMethod.PUT, RequestMethod.POST})
    public String potwierdz(
            @PathVariable("id") long idPojazdu,
            @Valid TransferData transferData,
            Model model,
            RedirectAttributes redirectAttributes) {
/*
        System.out.println(transferData.getIloscDni());
        System.out.println(id);*/

        model.addAttribute("pojazdy", pojazdRepository.findAll());
        model.addAttribute("errorMessage", "Login failed");

        Osoby customer = userRepository.getOne(userContext.getUserId());
        Pojazdy pojazd = pojazdRepository.getOne(idPojazdu);
        rentsRepository.save(new Rents(pojazd, customer, LocalDate.now().plusDays(transferData.getIloscDni())));
        return "redirect:/pojazdy";
    }

    @GetMapping("/contact")
    public String contactPage() {
        return "contact";
    }

    @GetMapping("/about")
    public String aboutPage() {
        return "about";
    }
}
