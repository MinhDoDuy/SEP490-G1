$(document).ready(function () {
    $('.quantity button').on('click', function () {
        var button = $(this);
        var input = button.parent().parent().find('input');
        var oldValue = input.val();
        var newVal;
        var myInput = 103;
        if (button.hasClass('btn-plus')) {
            newVal = parseFloat(oldValue) + 1;
        } else {
            if (oldValue > 1) {
                newVal = parseFloat(oldValue) - 1;
            } else {
                newVal = 1;
            }
        }
        input.val(newVal);
        console.log("Old Quantity: " + oldValue);
        console.log("New Quantity: " + newVal);
        console.log("CartId : " + myInput);
        updateCartItemQuantity(myInput, newVal);
    });
    $('.custom-input').on('change', function () {
        var input = $(this);
        var newVal = input.val();
        var cartItemId = input.data('cart-item-id');
        if (newVal < 1) {
            input.val(1);
            newVal = 1;
        }
        updateCartItemQuantity(cartItemId, newVal);
    });

    function updateCartItemQuantity(cartItemId, quantity) {
        $.ajax({
            url: '/update_cart_quantity',
            type: 'Get',
            data: {
                cartItemId: cartItemId,
                quantity: quantity
            },
            success: function (response) {
                console.log(response);
                location.reload(); // Reload the page to reflect the changes
            },
            error: function (error) {
                console.error("Error updating quantity:", error);
            }
        });
    }


});
