package com.api.springapiproject.Controller;

import com.api.springapiproject.Model.*;
import com.api.springapiproject.Repository.AccountRepository;
import com.api.springapiproject.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Email;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

@RestController
public class AccountController {
    HashMap<String,List<String>>req=new HashMap<>();
    HashMap<String,List<String>>reqf=new HashMap<>();
    List<String>q=new ArrayList<>();
    List<String>fq=new ArrayList<>();
    List<String>friend=new ArrayList<>();
    List<String>foll=new ArrayList<>();
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    UserRepository userRepository;
    private List<User> users;

    @PostMapping("/insert")
    public String okright(@RequestBody Account account){
       // accountRepository.save(account);
        q.clear();
        fq.clear();
        friend.clear();
        foll.clear();
        return "ok";
    }
    @GetMapping("/friend")
    public ResponseEntity<Object> search(){
//        List<String>lis=new ArrayList<>();
//        if(account.getResult()!=0){
//            Optional<Account> ac=accountRepository.findById(account.getMailId());
//
//        }
        q.clear();
        return ResponseEntity.accepted().body(accountRepository.findAll());
    }

    @GetMapping("/List")
    public ResponseEntity<Object> listing(){
        List<User> result = new ArrayList<>();
        result = userRepository.findAll();
        List<String> idlist = new ArrayList<>();
        for (User e : result) {
            idlist.add(e.getEmail());
        }
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(idlist);
    }

    @PostMapping("/req")
    public ResponseEntity<?> request(@RequestBody Request request){
        if(request.getFrom().equals(request.getTo())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Request to yourself is not accepted");
        }
        else {
            if(userRepository.existsById(request.getFrom()) && userRepository.existsById(request.getTo())) {

                if (req.containsKey(request.getFrom())) {
                    req.get(request.getFrom()).add(request.getTo());
                } else {
                    req.put(request.getFrom(), new ArrayList<String>());
                    req.get(request.getFrom()).add(request.getTo());
                }
                //req.clear();
                System.out.println(friend.contains(request.getFrom()));
                if(!friend.contains(request.getFrom())) {
                    //Optional<Account> account = accountRepository.findById(request.getTo());
                    Account ac = accountRepository.findByMailId(request.getTo());
                    q.add(request.getFrom());
                    ac.setRequests(q);
                    accountRepository.save(ac);
                    return ResponseEntity.status(HttpStatus.ACCEPTED).body(req);
                }
                else{
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Already a friend");
                }
            }
            else{
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The id is not matched");
            }
        }
    }

    @PostMapping("/friend/pass/{mail}")
    public ResponseEntity<Object> pass(@PathVariable String mail, @RequestBody Account account){
        long a=account.getResult();
        Account ac=accountRepository.findByMailId(account.getMailId());
        if(a>0){
            if(!friend.contains(mail)) {
                friend.add(mail);
                ac.setFriends(friend);
                q.remove(mail);
                ac.setRequests(q);
                accountRepository.save(ac);
                return ResponseEntity.status(HttpStatus.ACCEPTED).body("Added");
            }
            else{
                return ResponseEntity.status(HttpStatus.ACCEPTED).body("Already a friend");
            }
        }
        return ResponseEntity.status(HttpStatus.ACCEPTED).body("Not added");
    }

    @PostMapping("/follow/req")
    public ResponseEntity<?> following(@RequestBody Request requestf){
        if(requestf.getFrom().equals(requestf.getTo())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Request to yourself is not accepted");
        }
        else {
            if(userRepository.existsById(requestf.getFrom()) && userRepository.existsById(requestf.getTo())) {

                if (reqf.containsKey(requestf.getFrom())) {
                    reqf.get(requestf.getFrom()).add(requestf.getTo());
                } else {
                    reqf.put(requestf.getFrom(), new ArrayList<String>());
                    reqf.get(requestf.getFrom()).add(requestf.getTo());
                }
                //req.clear();
                System.out.println(friend.contains(requestf.getFrom()));
                if(!foll.contains(requestf.getFrom())) {
                    //Optional<Account> account = accountRepository.findById(request.getTo());
                    Account ac = accountRepository.findByMailId(requestf.getTo());
                    fq.add(requestf.getFrom());
                    ac.setFollow_request(fq);
                    accountRepository.save(ac);
                    return ResponseEntity.status(HttpStatus.ACCEPTED).body(reqf);
                }
                else{
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Already a friend");
                }
            }
            else{
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The id is not matched");
            }
        }
    }

    @PostMapping("/follow/pass/{mail}")
    public ResponseEntity<Object> follow(@PathVariable String mail, @RequestBody Account account){
        long a=account.getResult();
        Account ac=accountRepository.findByMailId(account.getMailId());
        if(a>0){
            if(!foll.contains(mail)) {
                foll.add(mail);
                ac.setFollowing(foll);
                fq.remove(mail);
                ac.setFollow_request(fq);
                accountRepository.save(ac);
                return ResponseEntity.status(HttpStatus.ACCEPTED).body("Following");
            }
            else{
                return ResponseEntity.status(HttpStatus.ACCEPTED).body("Already following");
            }
        }
        return ResponseEntity.status(HttpStatus.ACCEPTED).body("failed");
    }
}
