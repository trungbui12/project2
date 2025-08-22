/**
 * 
 */
document.querySelectorAll('.cart-item').forEach(item => {
	const minusBtn = item.querySelector('.btn-num-product-down');
	const plusBtn = item.querySelector('.btn-num-product-up');
	const quantityInput = item.querySelector('.quantity');
	const idInput = item.querySelector('.idCartDetail');
	const checkbox  = item.querySelector('.product-checkbox');
	checkbox.addEventListener('click', () => {
		updateCartTotal();
	});
	minusBtn.addEventListener('click', () => {
	    let qty = parseInt(quantityInput.value);
		let id = parseInt(idInput.value);
		if(qty > 1){
			quantityInput.value = qty - 1;
			window.location = window.location.href + "/update?id="+id + "&quantity="+(qty-1);
		}
	    
  	});
	plusBtn.addEventListener('click', () => {
		let qty = parseInt(quantityInput.value);
		let id = parseInt(idInput.value);
		qty = qty + 1;
		window.location = window.location.href + "/update?id="+id + "&quantity="+qty;
  	});
	quantityInput.addEventListener('change', () => {
		let qty = parseInt(quantityInput.value);
		let id = parseInt(idInput.value);
	    if (quantityInput.value < 1) quantityInput.value = 1;
		window.location = window.location.href + "/update?id="+id + "&quantity="+qty;
  	});
});

$("#voucher").on("change", function () {
    const selectedOption = $("#voucher option:selected"); // Lấy option được chọn
    const pct2 = selectedOption.data("percent"); // lấy data-percent

    if (pct2) {
        const productsTotal = parseInt(document.getElementById('totalAmount').textContent.replace(/[.,đ\s]/g, '')) || 0;
        const discountAmount = Math.floor((parseInt(pct2) / 100) * productsTotal);

        document.getElementById('discountAmount').textContent = '-' + discountAmount.toLocaleString() + ' đ';
    } else {
        document.getElementById('discountAmount').textContent = '-0 đ';
    }
    updateCartTotal();
});


function updateCartTotal() {
  let total = 0;
  const cartItems = document.querySelectorAll('.cart-item');

  cartItems.forEach(item => {
    const checkbox = item.querySelector('.product-checkbox');
    const price = parseInt(item.querySelector('.product-price').dataset.price);
    const quantity = parseInt(item.querySelector('.quantity').value);
    const lineTotalEl = item.querySelector('.line-total');

    const lineTotal = price * quantity;
    lineTotalEl.textContent = lineTotal.toLocaleString() + ' đ';

    if (checkbox.checked) {
      total += lineTotal;
    }
  });

  document.getElementById('totalAmount').textContent = total.toLocaleString() + ' đ';
  updateInforPaymentAmount();
}

function updateInforPaymentAmount() {
  // Lấy subtotal
  const subtotal = parseInt(document.getElementById('totalAmount').textContent.replace(/[.,đ\s]/g, '')) || 0;

  // Lấy phí ship
  const shippingFee = parseInt(document.getElementById('shippingFee').textContent.replace(/[.,đ\s]/g, '')) || 0;

  // Lấy discount
  const discountText = document.getElementById('discountAmount').textContent.replace(/[.,đ\s]/g, '');
  const discount = discountText ? Math.abs(parseInt(discountText)) : 0;

  // Tính tổng cuối cùng
  const total = subtotal + shippingFee - discount;

  // Cập nhật hiển thị
  document.getElementById('paymentAmount').textContent = total.toLocaleString() + ' đ';
}


$("#btn-payment").on("click",function(){
	const cartItems = document.querySelectorAll('.cart-item');
	const productsTotal = parseInt(document.getElementById('totalAmount').textContent.replace(/[.,]/g, ''));
	const discountAmount = parseInt(document.getElementById('discountAmount').textContent.replace(/[.,]/g, ''));
	const paymentAmount = parseInt(document.getElementById('paymentAmount').textContent.replace(/[.,]/g, ''));
	const shippingFee = parseInt(document.getElementById('shippingFee').textContent.replace(/[.,]/g, ''));
	const address = $('#address').val();	
	
	if(productsTotal == 0 || shippingFee == 0 || address == ""){
		swal("Thanh toán", "Chưa nhập đủ thông tin!", "error");
		return
	}else{
		$("#formpayment").submit();
	}
	
});
$("#paymentMethod").on("change", function() {
  const method = $(this).val();
  if (method === "VIETQR") {
    // Lấy số tiền cần thanh toán
    const amount = parseInt(document.getElementById('paymentAmount').textContent.replace(/[.,đ\s]/g, '')) || 0;

    // Thông tin tài khoản ngân hàng của shop
    const bankCode = "TCB"; // Ví dụ: Vietcombank
    const accountNo = "19070943135011";
    const accountName = "Ngân hàng Thương mại Cổ phần Kỹ thương Việt Nam";

    // API sinh QR VietQR
    const qrUrl = `https://img.vietqr.io/image/${bankCode}-${accountNo}-qr_only.png?amount=${amount}&addInfo=Thanh+toan+don+hang&accountName=${encodeURIComponent(accountName)}`;

    $("#vietqr-img").attr("src", qrUrl);
    $("#vietqr-section").show();
  } else {
    $("#vietqr-section").hide();
  }
});

async function checkout(shippingFee, discountPercent, total) {
	
	const orderData = {
	    
		shippingFee,
	    discountPercent,
	    total
	  };
	try {
	    const response = await fetch('/carts/checkout', {
	      method: 'POST',
	      headers: { 'Content-Type': 'application/json' },
	      body: JSON.stringify(orderData)
	    });
	    
	    if (response.ok) {
			
			const data = await response.json(); 
			console.log(data);
	      swal("Thanh toán",'Đặt hàng thành công!', "success")
		  .then(() => {
		            // Giả sử server trả về đối tượng có dạng: { order: { id: 123 } }
		            window.location.href = "http://localhost:8080/orders/" + data.order.id;
		          });
	    } else {
	      alert('Đặt hàng thất bại. Vui lòng thử lại.');
	    }
	  } catch (error) {
	    console.error('Lỗi khi gửi đơn hàng:', error);
	    alert('Đã xảy ra lỗi. Vui lòng thử lại sau.');
	  }
}
