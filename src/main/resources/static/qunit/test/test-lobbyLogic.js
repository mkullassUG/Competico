(function(qunit, $) {

	qunit.test("Who am I", function(assert) {
		assert.timeout(10000);
		$.mockjax({
			url: '/api/v1/playerinfo',
			response: function(settings) {
				//this.responseText = settings.data.response + ' 2';
				this.responseText = {
					"username": "awdawd",
					"nickname": "awdawd",
					"gameCode": "qYlwPnUH",
					"gameStarted": false,
					"isHost": true
				  };
			}
		});
		
		//nie zadziała przez asynchroniczność ajax'a

		//ponoć dobrym pomysłem jest użycie callbacku

		//czyli callback odpalany na samym końcy LobbyLogic albo GameLogic będzie miał metode tego asserta...
		var debug = LobbyLogic.getInstance(debug = true);

		var singleton = LobbyLogic.singleton;
		console.log(singleton)
		console.log(singleton.username)
		console.log(singleton.username == "awdawd")
		assert.equal(singleton.username, "awdawd");
	});
	
	qunit.test("Goodbye", function(assert) {
		assert.equal(sayGoodbye("Simon"), "Goodbye, Simon");
	});
})(window.QUnit, window.jQuery);
