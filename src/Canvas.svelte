<script>
  import {onMount} from 'svelte';
  import Konva from 'konva';

  export let nodes = [];

  let canvasContainer;

  function drawCanvas() {
    var stage = new Konva.Stage({
      container: canvasContainer,
      width: 800,
      height: 600,
    });

    if (true) {
      var nodesLayer = drawNodes(nodes)
    }

    stage.add(nodesLayer);
  }

  function drawNodes(nodes) {
    let layer = new Konva.Layer();

    for (var node of nodes) {
      let circle = new Konva.Circle({
        x: node.x,
        y: node.y,
        radius: 4,
        fill: 'white',
        stroke: 'blue',
        strokeWidth: 1,
      });
      layer.add(circle);
    }
    
    return layer
  }

  function addNode(event) {
    if (event.buttons == 1) { // LMB
      nodes.push({x: event.layerX, y: event.layerY})
    }
    console.log("click on canvas", event);
    drawCanvas();
  }

  onMount(drawCanvas)
  export function redraw() {
    drawCanvas()
  }
</script>

<div class="card">
  <div class="card-body">
    <div bind:this={canvasContainer} on:mousedown|preventDefault={addNode}/>
  </div>
</div>

<style>
</style>