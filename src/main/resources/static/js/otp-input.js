window.addEventListener("load", function () {
    // Get otp container
    const OTPContainer = document.querySelector("#otp-input");
    const OTPValueContainer = document.querySelector("#otp-value");
    const continueButton = document.querySelector("form button[type='submit']");

    continueButton.addEventListener("click", (e) => {
        e.preventDefault();
        updateValue(inputs);
        // Remove alert and submit the form
        document.querySelector('form').submit();
    });

    // Focus first input
    const firstInput = OTPContainer.querySelector("input");
    firstInput.focus();

    // OTP Logic
    const updateValue = (inputs) => {
        OTPValueContainer.value = Array.from(inputs).reduce((acc, curInput) => acc.concat(curInput.value ? curInput.value : "*"), "");
    };

    const isValidInput = (inputValue) => {
        return inputValue.length === 1 && /\d/.test(inputValue);
    };

    const setInputValue = (inputElement, inputValue) => {
        inputElement.value = inputValue;
    };

    const resetInput = (inputElement) => {
        setInputValue(inputElement, "");
    };

    const focusNext = (inputs, curIndex) => {
        const nextElement = curIndex < inputs.length - 1 ? inputs[curIndex + 1] : inputs[curIndex];
        nextElement.focus();
        nextElement.select();
    };

    const focusPrev = (inputs, curIndex) => {
        const prevElement = curIndex > 0 ? inputs[curIndex - 1] : inputs[curIndex];
        prevElement.focus();
        prevElement.select();
    };

    const handleInput = (inputElement, inputValue, curIndex, inputs) => {
        if (!isValidInput(inputValue)) return handleInvalidInput(inputElement);
        handleValidSingleInput(inputElement, inputValue, curIndex, inputs);
    };

    const handleValidSingleInput = (inputElement, inputValue, curIndex, inputs) => {
        setInputValue(inputElement, inputValue.slice(-1));
        focusNext(inputs, curIndex);
    };

    const handleInvalidInput = (inputElement) => {
        resetInput(inputElement);
    };

    const handleKeyDown = (event, key, inputElement, curIndex, inputs) => {
        if (key === "Delete" || key === "Backspace") {
            resetInput(inputElement);
            focusPrev(inputs, curIndex);
        }
        if (key === "ArrowLeft") {
            event.preventDefault();
            focusPrev(inputs, curIndex);
        }
        if (key === "ArrowRight") {
            event.preventDefault();
            focusNext(inputs, curIndex);
        }
    };

    const inputs = OTPContainer.querySelectorAll("input:not(#otp-value)");
    inputs.forEach((input, index) => {
        input.addEventListener("input", (e) => handleInput(input, e.target.value, index, inputs));
        input.addEventListener("keydown", (e) => handleKeyDown(e, e.key, input, index, inputs));
        input.addEventListener("focus", (e) => e.target.select());
    });
});
