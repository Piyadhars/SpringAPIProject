package com.api.springapiproject.Controller;

import java.util.*;
import com.api.springapiproject.Model.*;
import com.api.springapiproject.Repository.AccountRepository;
import com.api.springapiproject.Repository.UserRepository;
import com.api.springapiproject.Services.JwtService;
import com.api.springapiproject.Services.MailService;
import com.api.springapiproject.jwtauth.JwtUtil;
import jdk.jfr.Enabled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Optional;

@RestController
public class UserController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtService userDetailsService;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    JwtUtil jwtUtil;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    PasswordEncoder encoder;
    @Autowired
    private MailService emailSenderService;
    @GetMapping("/register")
    @ResponseBody
    public ResponseEntity<Object> showdetail() {
        return ResponseEntity.status(HttpStatus.OK).body(userRepository.findAll());
    }
    @PostMapping("/register")
    public Object registerUser(@RequestBody User user) {
        if(user.getEmail()!=null && user.getPassword()!=null &&user.getPassword().length()>8&& user.getFirstName()!=null &&
                user.getLastName()!=null && (user.getGender().equals("Male")||user.getGender().equals("Female"))&&
                user.getDate_of_birth()!=null){
            if(!userRepository.existsById(user.getEmail())) {
                List<String> d=new ArrayList<>();
                Account a=new Account(user.getEmail(), d, d, d, d, 0);
                accountRepository.save(a);
                user.setPassword(encoder.encode(user.getPassword()));
                Random r = new Random();
                int n = r.nextInt();
                String Hexadecimal = Integer.toHexString(n);
                user.setConfirmation_token(Hexadecimal);
                userRepository.save(user);
                SimpleMailMessage mailMessage = new SimpleMailMessage();
                mailMessage.setTo(user.getEmail());
                mailMessage.setSubject("Complete Registration!");
                mailMessage.setFrom("m.priyadharshinimanak@gmail.com");
                mailMessage.setText("To confirm your account, please click here : "
                        + "http://localhost:8082/confirm-account?token=" + user.getConfirmation_token());

                emailSenderService.sendEmail(mailMessage);
                return "Success";
            }
            else{
                return "The mail is already exists";
            }
        }
        else{
            return "Enter valid details" +
                    "\npassword should be atleast 8 characters " +
                    "\nall the fields must be filled";
        }
    }
    @RequestMapping(value="/confirm-account", method= {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public Object confirmUserAccount(@RequestParam("token")String confirmationToken)
    {
        User token = userRepository.findByConfirmation_token(confirmationToken);
        if(token != null)
        {
            token.setEnabled(true);
            userRepository.save(token);

        }
        else
        {
            return "message: The link is invalid or broken!";
        }
        return "Passed";
    }


    @RequestMapping(value="/login", method=RequestMethod.GET)
    @ResponseBody
    public Object displayLogin(ModelAndView modelAndView, User user) {
        modelAndView.addObject("user", user);
        modelAndView.setViewName("login");
        //return modelAndView;
        return "Ok";
    }

    @GetMapping("/checkUser")
    public String checkUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        return  currentPrincipalName;
    }

    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticate(@RequestBody AuthenticationRequest authenticationRequest) {
        try {
            Authentication authenticate = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authenticationRequest.getEmail(), authenticationRequest.getPassword())
            );
        }
        catch(BadCredentialsException ex){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getEmail());

        final String jwt = jwtUtil.generateToken(userDetails);

        return ResponseEntity.ok(new AuthenticationResponse(jwt));

    }


    @RequestMapping(value="/login", method=RequestMethod.POST)
    @ResponseBody
    public Object loginUser(ModelAndView modelAndView, @RequestBody User user) {

        Optional<User> existingUser = userRepository.findById(user.getEmail());
        System.out.println(existingUser);
        if(existingUser != null) {
            if (encoder.matches(user.getPassword(), existingUser.get().getPassword())){
                return "Success";
            } else {
                return "Failed-Incorrect Password";
            }
        } else {
            return "email does not exist";
        }
    }

    @RequestMapping(value="/forgot-password", method=RequestMethod.GET)
    @ResponseBody
    public Object displayResetPassword(ModelAndView modelAndView, User user) {
        modelAndView.addObject("user", user);
        modelAndView.setViewName("forgotPassword");
        //return modelAndView;
        return "Ok";
    }
    @RequestMapping(value="/forgot-password", method=RequestMethod.POST)
    @ResponseBody
    public Object forgotUserPassword(@RequestBody User user) {
        Optional<User> existingUser = userRepository.findById(user.getEmail());
        User read=userRepository.findByMail(user.getEmail());
        if(existingUser != null) {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(existingUser.get().getEmail());
            mailMessage.setSubject("Complete Password Reset!");
            mailMessage.setFrom("m.priyadharshinimanak@gmail.com");
            mailMessage.setText("To complete the password reset process, please click here: "
                    +"http://localhost:8082/confirm-reset?token="+read.getConfirmation_token());

            emailSenderService.sendEmail(mailMessage);
            read.setPassword(user.getPassword());
            read.setPassword(encoder.encode(user.getPassword()));
            userRepository.save(read);
            return "Success";

        } else {
            return "Email does not exist";
        }
    }


    @RequestMapping(value = "/reset-password", method = RequestMethod.POST)
    public Object resetUserPassword(User user) {

        if(user.getEmail() != null) {
            // use email to find user
            Optional<User> tokenUser = userRepository.findById(user.getEmail());
            tokenUser.get().setEnabled(true);
            tokenUser.get().setPassword(encoder.encode(user.getPassword()));
            // System.out.println(tokenUser.getPassword());
            userRepository.save(tokenUser.get());
//            modelAndView.addObject("message", "Password successfully reset. You can now log in with the new credentials.");
//            modelAndView.setViewName("successResetPassword");
        } else {
//            modelAndView.addObject("message","The link is invalid or broken!");
//            modelAndView.setViewName("error");
        }

        return "Success";
    }

   @PutMapping("/change")
    public Object proper(@RequestBody User user){
            if (user.getEmail() != null) {
                if (userRepository.existsById(user.getEmail())) {
                    Optional<User> exuser = userRepository.findById(user.getEmail());
                    User read = userRepository.findByMail(user.getEmail());
                    int flag = 0;
                    if (user.getGender() != null && !user.getGender().equals(exuser.get().getGender())) {
                        System.out.println(user.getGender().equals(exuser.get().getGender()));
                        read.setGender(user.getGender());
                        flag = 1;
                    } else if (user.getDate_of_birth() != null && !user.getDate_of_birth().equals(exuser.get().getDate_of_birth())) {
                        read.setDate_of_birth(user.getDate_of_birth());
                        flag = 1;
                    } else if (user.getFirstName() != null && !user.getFirstName().equals(exuser.get().getFirstName())) {
                        read.setFirstName(user.getFirstName());
                        flag = 1;
                    } else if (user.getLastName() != null && !user.getLastName().equals(exuser.get().getLastName())) {
                        read.setLastName(user.getLastName());
                        flag = 1;
                    }
                    if (flag == 1) {
                        userRepository.save(read);
                        return "Updated";
                    } else {
                        return "Failed" +
                                "\nAlready updated";
                    }
                } else {
                    return "failed" +
                            "/n enter valid update details like" +
                            "/n FirstName , LastName , DateOfBirth , Gender";
                }
            }
            return "Success";
   }

}
