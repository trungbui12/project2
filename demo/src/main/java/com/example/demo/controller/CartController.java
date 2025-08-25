package com.example.demo.controller;

import com.example.demo.entity.*;
import com.example.demo.repository.*;
import com.example.demo.service.EmailService;
import com.example.demo.service.GHNService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class CartController {
    @Autowired
    ProductRepository productRepository;
    @Autowired
    CartDetailRepository cartDetailRepository;
    @Autowired
    VoucherRepository voucherRepository;
    @Autowired
    HttpSession session;
    @Autowired
    GHNService ghnService;
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    OrderDetailRepository orderDetailRepository;
    @Autowired
    EmailService emailService;

    // ✅ Thêm size khi add vào giỏ
    @RequestMapping("/carts/add")
    public String add(RedirectAttributes redirectAttributes,
                      @RequestParam("quantity") Integer quantity,
                      @RequestParam("productSlug") String productSlug,
                      @RequestParam(value = "size", required = false) String size) { // ✅ size không bắt buộc

        Account account = (Account) session.getAttribute("account");
        if (account == null) {
            return "redirect:/login";
        }

        Product product = productRepository.findByActiveAndSlug(true, productSlug);
        if (product == null) {
            return "redirect:/carts/cartdemo";
        }

        // Nếu sản phẩm có size nhưng user không chọn → báo lỗi
        if (!product.getSizeList().isEmpty() && (size == null || size.isEmpty())) {
            redirectAttributes.addFlashAttribute("errorQuantity", "Please select a size");
            return "redirect:/products/" + productSlug;
        }

        CartDetail cartDetail = cartDetailRepository.findByAccountAndProductAndSize(account, product, size);
        if (cartDetail != null) {
            cartDetail.setQuantity(cartDetail.getQuantity() + quantity);
        } else {
            cartDetail = new CartDetail();
            cartDetail.setAccount(account);
            cartDetail.setProduct(product);
            cartDetail.setQuantity(quantity);
            cartDetail.setSize(size); // ✅ lưu size, có thể null nếu không có size
        }

        if (cartDetail.getQuantity() > product.getQuantity()) {
            redirectAttributes.addFlashAttribute("errorQuantity", "Số lượng không đủ");
            return "redirect:/products/" + productSlug;
        }
        cartDetailRepository.save(cartDetail);
        return "redirect:/carts/cartdemo";
    }


    @RequestMapping("/carts/cartdemo")
    public String cartdemo(Model model) {
        Account account = (Account) session.getAttribute("account");
        if (account == null) {
            return "redirect:/login";
        }
        List<CartDetail> cartDetails = cartDetailRepository.findByAccount(account);
        List<Integer> ids = new ArrayList<>();
        List<Order> orders = orderRepository.findByAccount(account);
        for (Order order : orders) {
            if (order.getVoucher() != null) {
                ids.add(order.getVoucher().getId());
            }
        }
        List<Voucher> vouchers = voucherRepository.findVoucherValidList(ids,
                Sort.by(Sort.Direction.DESC, "discountPercent"));
        model.addAttribute("cartDetails", cartDetails);
        model.addAttribute("vouchers", vouchers);
        return "cartdemo";
    }

    @RequestMapping("/carts/cartdemo/remove/{id}")
    public String removedemo(@PathVariable("id") Integer id) {
        Account account = (Account) session.getAttribute("account");
        if (account == null) {
            return "redirect:/login";
        }
        CartDetail cartDetail = cartDetailRepository.findById(id).orElse(null);
        if (cartDetail != null && account.getId() == cartDetail.getAccount().getId()) {
            cartDetailRepository.delete(cartDetail);
        }
        return "redirect:/carts/cartdemo";
    }

    @RequestMapping("/carts/cartdemo/update")
    public String updatedemo(RedirectAttributes redirectAttributes,
                             @RequestParam("id") Integer id,
                             @RequestParam("quantity") Integer quantity) {
        Account account = (Account) session.getAttribute("account");
        if (account == null) {
            return "redirect:/login";
        }
        CartDetail cartDetail = cartDetailRepository.findById(id).orElse(null);
        if (cartDetail != null && account.getId() == cartDetail.getAccount().getId()) {
            if (quantity > cartDetail.getProduct().getQuantity()) {
                redirectAttributes.addFlashAttribute("errorQuantity", cartDetail.getId());
                cartDetail.setQuantity(cartDetail.getProduct().getQuantity());
            } else {
                cartDetail.setQuantity(quantity);
            }
            cartDetailRepository.save(cartDetail);
        }
        return "redirect:/carts/cartdemo";
    }

    @RequestMapping("/carts")
    public String carts(Model model) {
        Account account = (Account) session.getAttribute("account");
        if (account == null) {
            return "redirect:/login";
        }
        List<CartDetail> cartDetails = cartDetailRepository.findByAccount(account);
        List<Integer> ids = new ArrayList<>();
        List<Order> orders = orderRepository.findByAccount(account);
        for (Order order : orders) {
            if (order.getVoucher() != null) {
                ids.add(order.getVoucher().getId());
            }
        }
        List<Voucher> vouchers = voucherRepository.findVoucherValidList(ids,
                Sort.by(Sort.Direction.DESC, "discountPercent"));
        model.addAttribute("cartDetails", cartDetails);
        model.addAttribute("vouchers", vouchers);
        return "cart";
    }

    @RequestMapping("/carts/remove/{id}")
    public String remove(@PathVariable("id") Integer id) {
        Account account = (Account) session.getAttribute("account");
        if (account == null) {
            return "redirect:/login";
        }
        CartDetail cartDetail = cartDetailRepository.findById(id).orElse(null);
        if (cartDetail != null && account.getId() == cartDetail.getAccount().getId()) {
            cartDetailRepository.delete(cartDetail);
        }
        return "redirect:/carts";
    }

    @RequestMapping("/carts/update")
    public String update(RedirectAttributes redirectAttributes,
                         @RequestParam("id") Integer id,
                         @RequestParam("quantity") Integer quantity) {
        Account account = (Account) session.getAttribute("account");
        if (account == null) {
            return "redirect:/login";
        }
        CartDetail cartDetail = cartDetailRepository.findById(id).orElse(null);
        if (cartDetail != null && account.getId() == cartDetail.getAccount().getId()) {
            if (quantity > cartDetail.getProduct().getQuantity()) {
                redirectAttributes.addFlashAttribute("errorQuantity", cartDetail.getId());
                cartDetail.setQuantity(cartDetail.getProduct().getQuantity());
            } else {
                cartDetail.setQuantity(quantity);
            }
            cartDetailRepository.save(cartDetail);
        }
        return "redirect:/carts";
    }

    // ✅ Checkout có lưu size vào OrderDetail
    @RequestMapping("/carts/checkout")
    public String checkout(@RequestParam("ids") String[] ids,
                           @RequestParam("customerName") String fullName,
                           @RequestParam("customerPhone") String phoneNumber,
                           @RequestParam("customerEmail") String email,
                           @RequestParam("provinceSelect") int provinceSelect,
                           @RequestParam("districtSelect") int districtSelect,
                           @RequestParam("wardSelect") String wardSelect,
                           @RequestParam("fullAddress") String fullAddress,
                           @RequestParam(value = "voucher", required = false) String voucherIdStr, // ✅ cho phép rỗng
                           @RequestParam("paymentMethod") String paymentMethod) {
        Account account = (Account) session.getAttribute("account");
        if (account == null) {
            return "redirect:/login";
        }

        int total = 0;
        for (String id : ids) {
            CartDetail cartDetail = cartDetailRepository.findById(Integer.parseInt(id)).orElse(null);
            if (cartDetail != null) {
                total += cartDetail.getQuantity() * cartDetail.getProduct().getPrice();
            }
        }

        // ✅ Xử lý voucher optional
        Voucher voucher = null;
        if (voucherIdStr != null && !voucherIdStr.isEmpty()) {
            voucher = voucherRepository.findById(Integer.parseInt(voucherIdStr)).orElse(null);
        }
        int discount = (voucher != null) ? total * voucher.getDiscountPercent() / 100 : 0;
        int feeShip = ghnService.getShippingFee(districtSelect, wardSelect);

        // Tạo order
        Order order = new Order();
        order.setCreatedAt(new Date());
        order.setAccount(account);
        order.setVoucher(voucher);
        order.setDiscount(discount);
        order.setFeeShip(feeShip);
        order.setShipAddress(fullAddress);
        order.setTotal(total);
        order.setFullName(fullName);
        order.setPhoneNumber(phoneNumber);
        order.setEmail(email);

        if (paymentMethod.equals("COD")) {
            order.setPaymentMethod(0);
            order.setPaymentStatus(0);
            order.setStatus(1);
        } else if (paymentMethod.equals("VIETQR")) {
            order.setPaymentMethod(1);
            order.setPaymentStatus(0);
            order.setStatus(1);
        }

        orderRepository.save(order);

        // --------------------- EMAIL CONTENT ---------------------
        String subject = "Order Confirmation #" + order.getId();

        StringBuilder content = new StringBuilder();
        content.append("<h3>Thank you for your order!</h3>")
                .append("<p><strong>Customer Name:</strong> ").append(fullName).append("</p>")
                .append("<p><strong>Phone Number:</strong> ").append(phoneNumber).append("</p>")
                .append("<p><strong>Shipping Address:</strong> ").append(fullAddress).append("</p>")
                .append("<p><strong>Payment Method:</strong> ").append(paymentMethod).append("</p>")
                .append("<hr/>")
                .append("<h4>Order Details:</h4>")
                .append("<table border='1' cellspacing='0' cellpadding='5' style='border-collapse:collapse;width:100%'>")
                .append("<tr>")
                .append("<th>Product</th>")
                .append("<th>Size</th>")
                .append("<th>Quantity</th>")
                .append("<th>Price</th>")
                .append("<th>Subtotal</th>")
                .append("</tr>");

        int grandTotal = 0;

        for (String id : ids) {
            CartDetail cartDetail = cartDetailRepository.findById(Integer.parseInt(id)).orElse(null);
            if (cartDetail != null) {
                OrderDetail orderDetail = new OrderDetail();
                orderDetail.setOrder(order);
                orderDetail.setProduct(cartDetail.getProduct());
                orderDetail.setPrice(cartDetail.getProduct().getPrice());
                orderDetail.setQuantity(cartDetail.getQuantity());
                orderDetail.setSize(cartDetail.getSize());
                orderDetailRepository.save(orderDetail);

                // Add product info to email
                int subtotal = cartDetail.getQuantity() * cartDetail.getProduct().getPrice();
                grandTotal += subtotal;
                content.append("<tr>")
                        .append("<td>").append(cartDetail.getProduct().getName()).append("</td>")
                        .append("<td>").append(cartDetail.getSize() == null ? "-" : cartDetail.getSize()).append("</td>")
                        .append("<td>").append(cartDetail.getQuantity()).append("</td>")
                        .append("<td>").append(cartDetail.getProduct().getPrice()).append("</td>")
                        .append("<td>").append(subtotal).append("</td>")
                        .append("</tr>");

                cartDetailRepository.delete(cartDetail);
            }
        }

        content.append("</table>")
                .append("<p><strong>Discount:</strong> -").append(discount).append(" đ</p>")
                .append("<p><strong>Shipping Fee:</strong> ").append(feeShip).append(" đ</p>")
                .append("<p><strong>Total Amount:</strong> ").append(grandTotal - discount + feeShip).append(" đ</p>");

        try {
            emailService.sendOrderConfirmation(account.getEmail(), subject, content.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        // --------------------- END EMAIL CONTENT ---------------------

        return "redirect:/orders/detail/" + order.getId();
    }

}
