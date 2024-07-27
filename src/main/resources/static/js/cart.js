$(document).ready(function () {
    let currentRequest = null;
    let debounceTimeout = null;
    $('.quantity button').on('click', function (event) {
        const button = $(this);
        const input = button.parent().parent().find('input');
        const oldValue = input.val();
        let newVal;
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
        const cartItemId = event.target.getAttribute('id');
        updateCartItemQuantity(cartItemId, newVal, input);
    });
    $('.custom-input').on('change', function (event) {
        const input = $(this);
        let newVal = input.val();
        const cartItemId = event.target.getAttribute('id');
        if (newVal < 1) {
            input.val(1);
            newVal = 1;
        }
        updateCartItemQuantity(cartItemId, newVal, input, true);
    });

    function updateCartItemQuantity(cartItemId, quantity, input, changeByHand = false) {
        const newCartItemId = changeByHand ? cartItemId.replace('quantity', '') : cartItemId;
        const element = document.getElementById('totalPrice' + newCartItemId);
        const perPrice = document.getElementById('perPrice' + newCartItemId)?.getAttribute('value') ?? 0;
        const maxQuantity = element.getAttribute('max-order') ?? 0;
        let priceUpdate;
        if (parseInt(quantity) > parseInt(maxQuantity)) {
            input.val(maxQuantity)
            priceUpdate = perPrice * maxQuantity;
            document.getElementById("totalPrice" + newCartItemId).innerHTML = formatCurrencyVND(priceUpdate.toString());
            if (changeByHand) {
                // Set a new debounce timeout
                debounceTimeout = setTimeout(function () {
                    // Make a new AJAX request
                    currentRequest = $.ajax({
                        url: '/update_cart_quantity',
                        type: 'Get',
                        data: {
                            cartItemId: newCartItemId,
                            quantity: quantity
                        },
                        success: function (response) {
                            // Do nothing
                            console.log(response);
                        },
                        error: function (error) {
                            if (error.statusText !== 'abort') {
                                console.error("Error updating quantity:", error);
                            }
                        }
                    });
                }, 100);
            }
            const err = document.getElementById('errorMessage')
            err.style.visibility = "visible";
            err.innerHTML = "Không được mua quá số lượng!";
            setTimeout(function () {
                err.style.visibility = "hidden";
            },3000);
            return;
        }

        priceUpdate = perPrice * quantity;

        document.getElementById("totalPrice" + newCartItemId).innerHTML = formatCurrencyVND(priceUpdate.toString());

        // Abort the previous request if it exists
        if (currentRequest) {
            console.log("Abort update price!!!")
            currentRequest.abort();
        }

        // Clear the previous debounce timeout
        clearTimeout(debounceTimeout);

        // Set a new debounce timeout
        debounceTimeout = setTimeout(function () {
            // Make a new AJAX request
            currentRequest = $.ajax({
                url: '/update_cart_quantity',
                type: 'Get',
                data: {
                    cartItemId: newCartItemId,
                    quantity: quantity
                },
                success: function (response) {
                    // Do nothing
                    console.log(response);
                },
                error: function (error) {
                    if (error.statusText !== 'abort') {
                        console.error("Error updating quantity:", error);
                    }
                }
            });
        }, 100);
    }
});

function formatCurrencyVND(amount) {
    return new Intl.NumberFormat('vi-VN', {
        style: 'currency',
        currency: 'VND'
    }).format(amount);
}

function updateQuantity(param1, param2, id) {
    // Do not implement at here !!!
}

function callAPI(currentRequest, newCartItemId, quantity) {

}
