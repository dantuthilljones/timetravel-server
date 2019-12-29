package me.detj.timetravel.controllers;

import com.google.common.base.Preconditions;
import me.detj.timetravel.dto.*;
import me.detj.timetravel.TimeTravelLogic;
import org.springframework.web.bind.annotation.*;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@RestController
public class TimeTravelController {

    private final TimeTravelLogic timeTravelLogic;

    public TimeTravelController(TimeTravelLogic timeTravelLogic) {
        this.timeTravelLogic = Preconditions.checkNotNull(timeTravelLogic, "timeTravel is null");
    }

    @GetMapping("/api/todays-words")
    @ResponseBody
    public List<String> todaysWords() throws InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, NoSuchPaddingException {
        return timeTravelLogic.todaysWords();
    }

    @GetMapping("/api/today")
    @ResponseBody
    public Date today() {
        return timeTravelLogic.today();
    }

    @PostMapping("/api/check")
    @ResponseBody
    public Result checkWords(@RequestBody List<String> words) {
        return timeTravelLogic.checkWords(words);
    }

    @PostMapping("/api/past-words")
    @ResponseBody
    public Page<PastStringsEntry> pastWords(@RequestBody PastStringsRequest request) {
        return timeTravelLogic.getPastWords(request);
    }
}
