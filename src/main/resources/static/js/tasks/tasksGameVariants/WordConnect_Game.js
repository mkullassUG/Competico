const WordConnect_Game = (taskData) => {
    var self = TaskGameVariant(taskData);
    self.taskName = "WordConnect";

    self.JsPlumb;

    var taskVariantInitSuper = self.taskVariantInit;
    self.taskVariantInit = (taskData) => {
      taskVariantInitSuper(taskData);
  
      self.answerCurrentlyAt = {};
      self.connections = {};
      self.endpointSources = [];
      self.endpointDestinations = [];
      self.words = taskData.leftWords;
      self.definitions = taskData.rightWords;
  
      var taskContentLeft = $(`<div class="leftSideC">`);
      var taskContentRight = $(`<div class="rightSideC">`);

      $("#GameDiv").html(``);
      for (let i = 0; i < self.words.length; i++) {
        var word = document.createTextNode(self.words[i]);
        var definition = document.createTextNode(self.definitions[i]);
        
        var leftLineC = $(`<div class="leftLineC">`);
        var wordNode = $(`<div class="word" data-order="`+i+`">`);
        wordNode.append(word);
        leftLineC.append(wordNode);

        taskContentLeft.append(leftLineC);

        var rightLineC = $(`<div class="rightLineC">`);
        var definitionNode = $(`<div class="definition" data-order="`+i+`">`);
        definitionNode.append(definition);
        rightLineC.append(definitionNode);

        taskContentRight.append(rightLineC);
      }

      var taskContentDiv = $(`<div id="taskContent">`);
      taskContentDiv.append(taskContentLeft);
      taskContentDiv.append(taskContentRight);

      
      $("#GameDiv").append(`<h6 class="border-bottom border-gray pb-2 mb-0 text-dark"> Content: </h6>`);
      $("#GameDiv").append(taskContentDiv);
      
      var wordDivs = $(".word"),
      definitionDivs = $(".definition");
      
      self.JsPlumbObject = (data) => {
          var that = {}
          that.instance = jsPlumb.getInstance();
          that.wordDivs;
          that.definitionDivs;
    
          that.init = (initData) => {

              that.wordDivs = initData.wordDivs;
              that.definitionDivs = initData.definitionDivs;
    
              var anEndpointDestination;
              var anEndpointSource;
              var wordDivs = that.wordDivs;
              for (let i = 0; i < wordDivs.length; i++) {
                  var wordDiv = $(wordDivs[i]);
                  anEndpointSource = {
                      endpoint: "Rectangle",
                      paintStyle:{fillStyle:"blue",radius:7,lineWidth:3},
                      hoverPaintStyle:{fillStyle:"darkblue",radius:7,lineWidth:3},
                      isSource: true,
                      isTarget: false,
                      maxConnections: 1,
                      anchor: [0, 0, 0, 0, 10, 0],
                      connectorStyle: {strokeStyle: that.colorPicker(i), lineWidth: 4},
                      connectorHoverStyle: {lineWidth: 6},
                      connector: "Bezier",
                  };
                  self.endpointSources.push(
                      that.instance.addEndpoint(
                          wordDiv, 
                          anEndpointSource
                      )
                  )
                  that.fixEndpoints(wordDiv);
              }
              var definitionDivs = that.definitionDivs;
              for (let i = 0; i < definitionDivs.length; i++) {
                  var definitionDiv = $(definitionDivs[i]);
                  anEndpointDestination = {
                      endpoint: "Dot",
                      paintStyle:{strokeStyle:"blue",fillStyle:"transparent",radius:7,lineWidth:3},
                      hoverPaintStyle:{strokeStyle:"blue",fillStyle:"blue",radius:7,lineWidth:3},  
                      isSource: false,
                      isTarget: true,
                      maxConnections: 1,
                      connectorStyle: {
                          dashstyle: "2 4"
                      },
                      anchor: [0, 0, 0, 0, -10, 0],
                      connectorStyle: {strokeStyle: that.colorPicker(i), lineWidth: 4},
                      connectorHoverStyle: {lineWidth: 6},
                      connector: "Bezier",
                      
                  };
                  self.endpointDestinations.push(
                      that.instance.addEndpoint(
                          definitionDiv, 
                          anEndpointDestination
                      )
                  )
                  that.fixEndpoints(definitionDiv);
              }
    
              that.instance.bind("connection", function(info,ev) {

                  self.connections[info.sourceId] = {
                      info: info,
                      ev: ev
                  }
              });
          }
    
          that.fixEndpoints = (parentnode) => {
      
              var endpoints = that.instance.getEndpoints(parentnode);
          
              var inputAr = $.grep(endpoints, function (elementOfArray, indexInArray) {
                  return elementOfArray.isSource; //input
              });
          
              var outputAr = $.grep(endpoints, function (elementOfArray, indexInArray) {
                  return elementOfArray.isTarget; //output
              });
          
              that.calculateEndpoint(inputAr, true);
              that.calculateEndpoint(outputAr, false);
          
              that.instance.repaintEverything();
          }
    
          that.calculateEndpoint = (endpointArray, isInput) => {
      
            var mult = 1 / (endpointArray.length + 1);
            for (var i = 0; i < endpointArray.length; i++) {
        
                if (isInput) {
        
                    endpointArray[i].anchor.x = 1;
                    endpointArray[i].anchor.y = mult * (i + 1);
                } else {
        
                    endpointArray[i].anchor.x = 0;
                    endpointArray[i].anchor.y = mult * (i + 1);
                }
            }
          }
    
          that.colorPicker = (iteration) => {
              if ( iteration % 9 == 0) return "purple";
              else if ( iteration % 9 == 0) return "gold";
              else if ( iteration % 8 == 0) return "cyan";
              else if ( iteration % 7 == 0) return "lime";
              else if ( iteration % 6 == 0) return "orange";
              else if ( iteration % 5 == 0) return "black";
              else if ( iteration % 4 == 0) return "pink";
              else if ( iteration % 3 == 0) return "yellow";
              else if ( iteration % 2 == 0) return "green";
              else if ( iteration % 1 == 0) return "blue";
              else return "red";
          }

          that.init(data);
          return that;
      }

      self.JsPlumb = self.JsPlumbObject({
          "wordDivs": wordDivs, 
          "definitionDivs": definitionDivs});
    }
  
    var getAnswersSuper = self.getAnswers;
    self.getAnswers = () => {
  
      var answers = {};
      
      var keys = Object.keys(self.connections);
  
      for (let i = 0; i < keys.length; i++) {
        var key = keys[i];
  
        var sourceText = $(self.connections[key].info.source[0]).text();
        var targetText = $(self.connections[key].info.target[0]).text();
        answers[sourceText] = targetText;
      }
      return  {"answerMapping": answers};
    }
  
    var resetSuper = self.reset;
    self.reset = () => {
        resetSuper();
  
        self.taskVariantInit(taskData);
    }

    var superResizeObserverVariantFunction = self.ResizeObserverVariantFunction;
    self.ResizeObserverVariantFunction = () => {
        superResizeObserverVariantFunction();

        var wordDivs = self.JsPlumb.wordDivs,
        definitionDivs = self.JsPlumb.definitionDivs;

        for (let i = 0; i < wordDivs.length; i++) {
            var wordDiv = $(wordDivs[i]);
            
            self.JsPlumb.fixEndpoints(wordDiv);
        }
        for (let i = 0; i < definitionDivs.length; i++) {
            var definitionDiv = $(definitionDivs[i]);
            
            self.JsPlumb.fixEndpoints(definitionDiv);
        }
    }
    
    self.taskVariantInit(taskData);
  
    return self;
}