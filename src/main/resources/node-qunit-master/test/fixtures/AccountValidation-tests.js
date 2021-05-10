QUnit.module( "Account validation module", {
    before: function() {

    }
});

test("Singleton test", function (assert) {
    
    var avgi = AccountValidation.getInstance() ;
    var av = AccountValidation();

    assert.equal(avgi === av, true, "We expect value to be true");
});

test("Invalid email data for registration", function (assert) {
    
    var av = AccountValidation();

    assert.equal(av.emailValid("123"), false, "We expect value to be false");
});
test("Valid email data for registration", function (assert) {
    
    var av = AccountValidation();

    assert.equal(av.emailValid("testEmail@gmail.com"), true, "We expect value to be true");
});



test("Invalid password data for registration", function (assert) {
    
    var av = AccountValidation();

    assert.equal(av.passwordValid("123"), false, "We expect value to be false");
});
test("Valid password data for registration", function (assert) {
    
    var av = AccountValidation();

    assert.equal(av.passwordValid("qazwsx!Q2"), true, "We expect value to be true");
});


test("Invalid password2 data for registration", function (assert) {
    
    var av = AccountValidation();

    assert.equal(av.password2Valid("qazwsx!Q2", "123"), false, "We expect value to be false");
});
test("Valid password2 data for registration", function (assert) {
    
    var av = AccountValidation();

    assert.equal(av.password2Valid("qazwsx!Q2", "qazwsx!Q2"), true, "We expect value to be true");
});



test("Invalid username data for registration", function (assert) {
    
    var av = AccountValidation();

    assert.equal(av.usernameValid("123"), false, "We expect value to be false");
});
test("Valid username data for registration", function (assert) {
    
    var av = AccountValidation();

    assert.equal(av.usernameValid("gracz1"), true, "We expect value to be true");
});



test("Invalid nickname data for registration", function (assert) {
    
    var av = AccountValidation();

    assert.equal(av.nicknameValid("12"), false, "We expect value to be false");
});
test("Valid nickname data for registration", function (assert) {
    
    var av = AccountValidation();

    assert.equal(av.nicknameValid("gracz1"), true, "We expect value to be true");
});



test("Invalid all data for registration", function (assert) {
    
    var av = AccountValidation();
    var data = {
        username: "123",
        password: "qazwsx!Q",
        password2: "qazwsx!",
        email: "testEmailgmail.com"
    }

    var expected = {
        valid: false, 
        username: false, 
        password: false, 
        password2: false,
        email: false
    }
    assert.deepEqual(av.validateData(data), expected, "We expect value to be as expected");
});
test("Valid all data for registration", function (assert) {
    
    var av = AccountValidation();

    var data = {
        username: "gracz1",
        password: "qazwsx!Q2",
        password2: "qazwsx!Q2",
        email: "testEmail@gmail.com"
    }

    var expected = {
        valid: true, 
        username: true,
        password: true,
        password2: true,
        email: true
    }
    assert.deepEqual(av.validateData(data), expected, "We expect value to be as expected");
});


