async function loadProvinces() {
  const res = await fetch("/ghn/provinces");
  const data = await res.json();
  let html = `<option selected disabled>--Chọn tỉnh--</option>`;
  data.forEach(p => html += `<option value="${p.ProvinceID}">${p.ProvinceName}</option>`);
  document.getElementById("province").innerHTML = html;
}

document.getElementById("province").addEventListener("change", async e => {
  const provinceId = e.target.value;
  const res = await fetch("/ghn/districts", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ province_id: parseInt(provinceId) })
  });
  const data = await res.json();
  let html = `<option selected disabled>--Chọn quận--</option>`;
  data.forEach(d => html += `<option value="${d.DistrictID}">${d.DistrictName}</option>`);
  document.getElementById("district").innerHTML = html;
  document.getElementById("ward").innerHTML = `<option selected disabled>--Chọn phường--</option>`;
});

document.getElementById("district").addEventListener("change", async e => {
  const districtId = e.target.value;
  const res = await fetch("/ghn/wards", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ district_id: parseInt(districtId) })
  });
  const data = await res.json();
  let html = `<option selected disabled>--Chọn phường--</option>`;
  data.forEach(w => html += `<option value="${w.WardCode}">${w.WardName}</option>`);
  document.getElementById("ward").innerHTML = html;
});

document.getElementById("ward").addEventListener("change", getFee);

async function getFee() {
  const districtId = parseInt(document.getElementById("district").value);
  const wardCode = document.getElementById("ward").value;
  const weight = 1000;

  if (!districtId || !wardCode) {
    document.getElementById("feeText").innerText = "Vui lòng chọn đầy đủ địa chỉ.";
    return;
  }

  const res = await fetch("/ghn/fee", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      to_district_id: districtId,
      to_ward_code: wardCode,
      weight: weight
    })
  });
  
  
  const fee = await res.json();
  document.getElementById("feeText").innerText = "Phí vận chuyển: " + fee + " đ";
  const scope = angular.element(document.querySelector('[ng-controller="CartController"]')).scope();
  scope.$apply(() => {
    scope.shippingFee = parseInt(fee);
  });
}


loadProvinces();
