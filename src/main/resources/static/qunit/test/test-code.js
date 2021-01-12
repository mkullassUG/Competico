(function(qunit, $) {

	qunit.test("Hello", function(assert) {
		assert.equal(sayHello("Simon"), "Hello, Simon");
	});
	
	qunit.test("Goodbye", function(assert) {
		assert.equal(sayGoodbye("Simon"), "Goodbye, Simon");
	});
})(window.QUnit, window.jQuery);
