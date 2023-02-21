const ChronologicalOrder_Game = (taskData) => {
    var self = TaskGameVariant(taskData);
    self.taskName = "ChronologicalOrder";
  
    var taskVariantInitSuper = self.taskVariantInit;
    self.taskVariantInit = (taskData) => {
      taskVariantInitSuper(taskData);
  
      self.answerCurrentlyAt = {};
      self.sentences = taskData.sentences;
  
      var taskContentReady = $(`<ul id="sortable" class="ui-sortable">`);
      for (let i = 0; i < self.sentences.length; i++) {
        var sentence = self.sentences[i];
        var li = $(`<li class="ui-state-default ui-sortable-handle">`);
        li.append($(`<span class="ui-icon-left ui-icon ui-icon-arrowthick-2-n-s">`));
        li.append(document.createTextNode(sentence));
        li.append($(`<span class="ui-icon-right ui-icon float-right ui-icon-arrowthick-2-n-s">`));
        taskContentReady.append(li);
      }
      $("#GameDiv").html(``);
      $("#GameDiv").append($(`<h6 class="border-bottom border-gray pb-2 mb-0 text-dark"> Content: </h6>`));
      var taskContentDiv = $(`<div id="taskContent">`);
      taskContentDiv.append(taskContentReady)
      $("#GameDiv").append(taskContentDiv);
      
      
      $( "#sortable" ).sortable({
        zIndex: 9999,
        placeholder: "ui-state-highlight",
        forcePlaceholderSize: true,
        containment: "#GameWrapperDiv",
        appendTo: document.body,
        cursorAt: { top: 17 }
      });
      var appendTo = $( "#sortable" ).sortable( "option", "appendTo" );
    }
  
    var getAnswersSuper = self.getAnswers;
    self.getAnswers = () => {
      var answers = getAnswersSuper();
  
      var liElements =  $("#sortable").find("li");
  
      for(let i = 0; i < liElements.length; i++) {
        var li = liElements[i];
        var sentence = $(li).clone()    //clone the element
        .children() //select all the children
        .remove()   //remove all the children
        .end()  //again go back to selected element
        .text();
  
        answers.push(sentence);
      }
  
      return {"answers": answers};
    }
  
    var resetSuper = self.reset;
    self.reset = () => {
      resetSuper();
  
      self.taskVariantInit(taskData);
    }
  
    self.taskVariantInit(taskData);
  
    return self;
}