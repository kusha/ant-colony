<script>
  import {onMount} from 'svelte';
  import Konva from 'konva';

  export let posX = 50;
  export let posY = 50;

  let canvasContainer;

  function drawCanvas() {
    var stage = new Konva.Stage({
      container: canvasContainer,
      width: 800,
      height: 600,
    });

    var layer = new Konva.Layer();

    var circle = new Konva.Circle({
      x: posX,
      y: posY,
      radius: 4,
      fill: 'white',
      stroke: 'blue',
      strokeWidth: 1,
    });

    // add the shape to the layer
    layer.add(circle);

    // add the layer to the stage
    stage.add(layer);

  }

  onMount(drawCanvas)
  export function redraw() {
    drawCanvas()
  }
</script>

<div class="card">
  <div class="card-body">
    <div bind:this={canvasContainer}/>
  </div>
</div>

<style>
</style>