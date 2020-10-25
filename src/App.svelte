<script>
  import Options from './Options.svelte';
  import Stats from './Stats.svelte';
  import Canvas from './Canvas.svelte';
  import ViewControls from './ViewControls.svelte';
  import MapControls from './MapControls.svelte';
  import MapLoader from './MapLoader.svelte';
  import AlgorithmControls from './AlgorithmControls.svelte';

  let options = {
    "b": 2.0
  }

  let canvas;
  let algorithm = "ant-colony";
  let nodes = [
    {x: 50, y: 200},
    {x: 50, y: 150},
    {x: 20, y: 250},
    {x: 50, y: 100}
  ]
  let nodesCount;
  $: nodesCount = nodes.length;
  
  let mode = "edit"; // edit / result
  
  function run() {
    // run ant colony
    console.log(nodes);
  }
</script>

<main>
	<div class="container-fluid">
		<div class="row">
		  <div class="col-3">
        <Options bind:options={options} />
        <Stats/>
		  </div>
		  <div class="col-9">
        <div class="d-flex justify-content-between">
          <ViewControls/>
          <MapControls nodesCount={nodesCount}/>
        </div>
        <Canvas bind:this={canvas} nodes={nodes}/>
        <div class="d-flex justify-content-between">
          <MapLoader/>
          <AlgorithmControls bind:algorithm={algorithm} on:run={run}/>
        </div>
		  </div>
		</div>
	</div>
</main>

<style>
  main {
    margin: 50px;
  }
  :global(.card, .form-row) {
    margin-bottom: 20px;
  }
</style>