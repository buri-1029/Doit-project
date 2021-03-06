package com.ssafy.doit.controller;

import com.ssafy.doit.model.Mileage;
import com.ssafy.doit.model.store.ChatRoom;
import com.ssafy.doit.model.store.Product;
import com.ssafy.doit.model.request.RequestPage;
import com.ssafy.doit.model.response.ResponseBasic;
import com.ssafy.doit.model.response.ResponseProduct;
import com.ssafy.doit.model.store.ProductStatus;
import com.ssafy.doit.model.store.Sale;
import com.ssafy.doit.model.user.User;
import com.ssafy.doit.repository.MileageRepository;
import com.ssafy.doit.repository.store.ChatRoomJoinRepository;
import com.ssafy.doit.repository.store.ChatRoomRepository;
import com.ssafy.doit.repository.store.ProductRepository;
import com.ssafy.doit.repository.UserRepository;
import com.ssafy.doit.repository.store.SaleRepository;
import com.ssafy.doit.service.S3Service;
import com.ssafy.doit.service.user.UserService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/product")
public class ProductController {
    private static final String DEFAULT_CURRENT_PAGE = "1";
    private static final String DEFAULT_PER_PAGE = "9";
    private static final String DEFAULT_DIRECTION = "DESC";

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MileageRepository mileageRepository;

    @Autowired
    private S3Service s3Service;

    @Autowired
    private SaleRepository saleRepository;

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private ChatRoomJoinRepository chatRoomJoinRepository;

    @ApiOperation(value = "?????? ??????")
    @PostMapping("/createProduct")
    public Object createProduct(@RequestBody Product product){
        ResponseBasic result = null;
        try{
            long userPk = userService.currentUser();
            User currentUser = userRepository.findById(userPk).get();
            int groupCount = currentUser.getGroupList().size();

            if(groupCount < 2) throw new Exception("?????? ?????? ??? ??????");
            product.setUser(currentUser);

            product = productRepository.save(product);
            result = new ResponseBasic(true, "success", product);
        }catch (Exception e){
            e.printStackTrace();
            result = new ResponseBasic(false, e.getMessage(), null);
        }

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @ApiOperation(value = "?????? ????????? ??????/??????")
    @PostMapping("/image")
    public Object productImage(@RequestParam Long pid, @RequestParam MultipartFile file) {
        ResponseBasic result = null;
        try{
            Product product = productRepository.findById(pid).get();
            String mediaPath = s3Service.upload(product.getImage(), file);
            product.setImage(mediaPath);
            productRepository.save(product);

            result = new ResponseBasic(true, "success", product);
        }
        catch (Exception e){
            e.printStackTrace();
            result = new ResponseBasic(false, e.getMessage(), null);
        }

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @ApiOperation(value = "?????? ??????")
    @PutMapping("/{id}")
    public Object modifyProduct(@PathVariable Long id, @RequestBody Product product){
        ResponseBasic result = null;
        try{
            long currentUser = userService.currentUser();
            Product origin = productRepository.findById(id).get();

            if(origin.getUser().getId() != currentUser) throw new Exception("?????? ?????????");

            origin.setTitle(product.getTitle());
            origin.setContent(product.getContent());
            origin.setImage(product.getImage());
            origin.setCategory(product.getCategory());

            productRepository.save(origin);
            result = new ResponseBasic(true, "success", origin);
        }catch (Exception e){
            e.printStackTrace();
            result = new ResponseBasic(false, e.getMessage(), null);
        }

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @ApiOperation(value = "?????? ??????")
    @DeleteMapping("/{id}")
    public Object createProduct(@PathVariable Long id){
        ResponseBasic result = null;
        try{
            long currentUser = userService.currentUser();
            Product origin = productRepository.findById(id).get();

            if(origin.getUser().getId() != currentUser) throw new Exception("?????? ?????????");
            s3Service.deleteFile(origin.getImage());
            productRepository.delete(origin);
            result = new ResponseBasic(true, "success", null);
        }catch (Exception e){
            e.printStackTrace();
            result = new ResponseBasic(false, e.getMessage(), null);
        }

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @ApiOperation(value = "?????? ?????? ??????")
    @GetMapping("/{id}")
    public Object detail(@PathVariable Long id){
        ResponseBasic result = null;

        try{
            ResponseProduct product = productRepository.getById(id);
            result = new ResponseBasic(true, "success", product);
        }
        catch (Exception e){
            e.printStackTrace();
            result = new ResponseBasic(false, e.getMessage(), null);
        }

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @ApiOperation(value = "?????? ?????? ??????")
    @GetMapping("/search")
    public Object search(@RequestParam(defaultValue = DEFAULT_CURRENT_PAGE) int pg,
                         @RequestParam(defaultValue = DEFAULT_DIRECTION) String direction,
                         @RequestParam(required = false) String option, @RequestParam(required = false) String keyword) {
        ResponseBasic result = null;
        try{
            RequestPage page = new RequestPage();
            page.setPage(pg);
            page.setSize(Integer.parseInt(DEFAULT_PER_PAGE));
            page.setDirection(direction.equals("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC);

            Page<ResponseProduct> products;
            if("user".equals(option)) products = productRepository.findAllByUserNicknameContaining(keyword, page.of());
            else if("category".equals(option)) products = productRepository.findAllByCategory(keyword, page.of());
            else if("title".equals(option)) products = productRepository.findAllByTitleContaining(keyword, page.of());
            else products = productRepository.findAllBy(page.of());

            result = new ResponseBasic(true, "success", products);
        }
        catch (Exception e){
            e.printStackTrace();
            result = new ResponseBasic(false, e.getMessage(), null);
        }

        return new ResponseEntity<>(result, HttpStatus.OK);
    }
    

    @ApiOperation(value = "?????? ?????? ??????")
    @GetMapping("/requestSell")
    public Object sell(@RequestParam Long roomPk){
        ResponseBasic result = null;
        try{
            User host = userRepository.findById(userService.currentUser()).get();
            ChatRoom chatRoom = chatRoomRepository.findById(roomPk).get();
            Product product = chatRoom.getProduct();

            if(host.getId() != product.getUser().getId()) throw new Exception("?????? ?????????");
            if(saleRepository.findByChatRoom_Product_Id(product.getId()).isPresent()) throw new Exception("?????? ???????????? ??????");

            Sale sale = new Sale();
            sale.setChatRoom(chatRoom);
            saleRepository.save(sale);

            product.setStatus(ProductStatus.WAITING);
            productRepository.save(product);

            result = new ResponseBasic(true, "success", null);
        }
        catch (Exception e){
            e.printStackTrace();
            result = new ResponseBasic(false, e.getMessage(), null);
        }

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @ApiOperation(value = "?????? ??????")
    @GetMapping("/purchase")
    public Object purchase(@RequestParam Long roomPk) {
        ResponseBasic result = null;
        try {
            ChatRoom chatRoom = chatRoomRepository.findById(roomPk).get();
            Product product = chatRoom.getProduct();

            User consumer = userRepository.findById(userService.currentUser()).get();
            User host = product.getUser();

            if (!chatRoomJoinRepository.findChatRoomJoinByUser_IdAndChatRoom_Id(consumer.getId(), roomPk).isPresent())
                throw new Exception("?????? ?????????");
            if(consumer.getMileage() < product.getMileage()) throw new Exception("???????????? ??????");

            Sale sale = saleRepository.findByChatRoom_Id(roomPk).get();
            saleRepository.delete(sale);

            for(ChatRoom c : product.getChatRooms())
                chatRoomRepository.delete(c);

            host.setMileage(host.getMileage() + product.getMileage());
            userRepository.save(host);
            mileageRepository.save(Mileage.builder()
                    .content("???????????? ?????? ?????? ??????")
                    .date(LocalDateTime.now())
                    .mileage("+" + product.getMileage())
                    .user(host).build());

            consumer.setMileage(consumer.getMileage() - product.getMileage());
            userRepository.save(consumer);
            mileageRepository.save(Mileage.builder()
                    .content("???????????? ?????? ?????? ??????")
                    .date(LocalDateTime.now())
                    .mileage("-" + product.getMileage())
                    .user(consumer).build());

            product.setStatus(ProductStatus.SOLDOUT);
            productRepository.save(product);

            result = new ResponseBasic(true, "success", null);

        } catch (Exception e){
            e.printStackTrace();
            result = new ResponseBasic(false, e.getMessage(), null);
        }

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @ApiOperation(value = "?????? ??????")
    @GetMapping("/cancel")
    public Object cancel(@RequestParam Long roomPk){
        ResponseBasic result = null;
        try {
            User currentUser = userRepository.findById(userService.currentUser()).get();
            if (!chatRoomJoinRepository.findChatRoomJoinByUser_IdAndChatRoom_Id(currentUser.getId(), roomPk).isPresent())
                throw new Exception("?????? ?????????");

            ChatRoom chatRoom = chatRoomRepository.findById(roomPk).get();
            Sale sale = saleRepository.findByChatRoom_Id(chatRoom.getId()).get();
            saleRepository.delete(sale);

            Product product = chatRoom.getProduct();
            product.setStatus(ProductStatus.ONSALE);
            productRepository.save(product);

            result = new ResponseBasic(true, "success", null);

        } catch (Exception e){
            e.printStackTrace();
            result = new ResponseBasic(false, e.getMessage(), null);
        }

        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
