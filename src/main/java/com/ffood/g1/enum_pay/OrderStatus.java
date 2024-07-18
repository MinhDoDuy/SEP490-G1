package com.ffood.g1.enum_pay;

public enum OrderStatus {
    PENDING,
    PROGRESS,
    COMPLETE,
    REJECT,

    //PENDING , PREPAIR , Ready(làm xong đồ ăn đến lấy + gửi mail) , Complete ( nhận hàng thành công  + gửi mail )  , Cancel(hủy = gửi mail đơn hàng đã bị hủy)
}
