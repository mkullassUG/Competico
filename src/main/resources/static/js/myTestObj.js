const myObj = ($) => {
    var self = {};
  
    var init = () => {
  
    }
  
    self.sayHello = () => console.log("hello");
  
    self.getHello = () => "hello";
  
    self.getHelloWithText = (text) => "hello" + text;
  
    self.getEmailfeedback = () => $('#emailfeedback').html();
  
    init();
    return self;
}

if (typeof module !== 'undefined' && typeof module.exports !== 'undefined')
    module.exports = {myObj};
else
    window.myObj = myObj;