/**
 * 
 */
/**
 * 
 */
const TOKEN = 'e5863113-7b6e-11f0-9f15-fe0c36d2f0cb';
const SHOP_ID = 5956446;
$(document).ready(function () {
  // Khởi tạo Select2
  $('.js-select2').select2();

  // Load danh sách Tỉnh/Thành phố khi trang được tải
  loadProvinces();

  // Sự kiện khi chọn Tỉnh/Thành phố
  $('#provinceSelect').on('change', function () {
    const provinceId = $(this).val();
    $('#districtSelect').prop('disabled', true).html('<option value="">--District--</option>').trigger('change');
    $('#wardSelect').prop('disabled', true).html('<option value="">--Ward--</option>').trigger('change');
    if (provinceId) {
      loadDistricts(provinceId);
    }
  });

  // Sự kiện khi chọn Quận/Huyện
  $('#districtSelect').on('change', function () {
    const districtId = $(this).val();
    $('#wardSelect').prop('disabled', true).html('<option value="">--Ward--</option>').trigger('change');
    if (districtId) {
      loadWards(districtId);
    }
  });
  // Sự kiện khi chọn Xã phường
  $('#wardSelect').on('change', async function () {
      const toDistrict = $('#districtSelect').val();
      const toWard = $(this).val();
      const serviceId = 53320; // ví dụ: chọn GHN Express
      // Lấy địa chỉ gửi cố định từ Shop (có thể lưu sẵn hoặc lấy động)
      const fromDistrict = 1574; //id quận cái răng
      const fromWard = '550307'; //phường thường thạnh

      // Lấy danh sách sản phẩm trong giỏ dưới dạng mảng item
      const items = [];
      $('.cart-item').each((_, row) => {
        const qty = parseInt($(row).find('.quantity').val(), 10);
        const name = $(row).find('.product-name').text().trim();
        const price = parseInt($(row).find('.product-price').data('price'), 10);
        items.push({ name, quantity: qty, price, length: 20, width: 10, height: 5, weight: 200 });
      });
		if(toDistrict && toWard){
			await calculateShippingFee(toDistrict, toWard, serviceId, items );
		}
      
    });
	
	$('#address').on('change', function(){
		let province = $('#provinceSelect  option:selected').text();
		let district = $('#districtSelect  option:selected').text();
		let ward = $('#wardSelect  option:selected').text();
		let address = $('#address').val();
		let fullAddress = address + ", " + ward + ", " + district + ", " + province;
		$('#fullAddress').val(fullAddress);
	});
});

// Hàm tải danh sách Tỉnh/Thành phố
async function loadProvinces() {
  try {
    const response = await fetch('https://online-gateway.ghn.vn/shiip/public-api/master-data/province', {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        'Token': TOKEN
      }
    });
    const result = await response.json();
    if (result.code === 200) {
      const provinces = result.data;
      provinces.forEach(province => {
        $('#provinceSelect').append(new Option(province.ProvinceName, province.ProvinceID));
      });
      $('#provinceSelect').trigger('change');
    } else {
      alert('Lỗi khi tải danh sách Tỉnh/Thành phố: ' + result.message);
    }
  } catch (error) {
    console.error('Lỗi khi gọi API Tỉnh/Thành phố:', error);
  }
}

// Hàm tải danh sách Quận/Huyện
async function loadDistricts(provinceId) {
  try {
    const response = await fetch('https://online-gateway.ghn.vn/shiip/public-api/master-data/district', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Token': TOKEN
      },
      body: JSON.stringify({ province_id: parseInt(provinceId) })
    });
    const result = await response.json();
    if (result.code === 200) {
      const districts = result.data;
      districts.forEach(district => {
        $('#districtSelect').append(new Option(district.DistrictName, district.DistrictID));
      });
      $('#districtSelect').prop('disabled', false).trigger('change');
    } else {
      alert('Lỗi khi tải danh sách Quận/Huyện: ' + result.message);
    }
  } catch (error) {
    console.error('Lỗi khi gọi API Quận/Huyện:', error);
  }
}


// Hàm tải danh sách Phường/Xã
async function loadWards(districtId) {
  try {
    const response = await fetch('https://online-gateway.ghn.vn/shiip/public-api/master-data/ward?district_id=' + districtId, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        'Token': TOKEN
      }
    });
    const result = await response.json();
    if (result.code === 200) {
      const wards = result.data;
      wards.forEach(ward => {
        $('#wardSelect').append(new Option(ward.WardName, ward.WardCode));
      });
      $('#wardSelect').prop('disabled', false).trigger('change');
    } else {
      alert('Lỗi khi tải danh sách Phường/Xã: ' + result.message);
    }
  } catch (error) {
    console.error('Lỗi khi gọi API Phường/Xã:', error);
  }
}
// Sau khi chọn wardSelect xong, gọi hàm này để tính phí
async function calculateShippingFee(toDistrict, toWard, serviceId, items ) {

  const payload = {
    from_district_id: 1574,
    from_ward_code: '550307',
    to_district_id: parseInt(toDistrict),
    to_ward_code: toWard,
    service_id: 53322,
    service_type_id: 2,
    height: 10,      // Ví dụ cố định, bạn có thể lấy từ dữ liệu SP
    length: 20,
    weight: 200,
    width: 20,
    insurance_value: 0,
    cod_failed_amount: 0,
    coupon: null,
    items: items    // Mảng các sản phẩm trong giỏ
  };

  try {
    const resp = await fetch('https://online-gateway.ghn.vn/shiip/public-api/v2/shipping-order/fee', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Token': TOKEN,
        'ShopId': SHOP_ID
      },
      body: JSON.stringify(payload)
    });
    const result = await resp.json();

    if (result.code === 200) {
      const fee = result.data.total;         // tổng cước GHN trả về
      document.getElementById('shippingFee').textContent = fee.toLocaleString() + " đ";;
      // Cập nhật tổng đơn hàng gồm sản phẩm + vận chuyển
      /*const productsTotal = parseInt(document.getElementById('totalAmount').textContent.replace(/,/g, ''));
      const discountAmount = parseInt(document.getElementById('discountAmount').textContent.replace(/,/g, ''));
	  document.getElementById('paymentAmount').textContent =
        (productsTotal + fee - discountAmount).toLocaleString() + " đ";*/
		updateInforPaymentAmount();
    } else {
      console.error('Lỗi tính phí vận chuyển:', result.message);
      alert('Không tính được phí vận chuyển: ' + result.message);
    }
  } catch (err) {
    console.error('Lỗi fetch phí vận chuyển:', err);
    alert('Lỗi kết nối khi tính phí vận chuyển.');
  }
}

