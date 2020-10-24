<script>
  import Options from './Options.svelte';
  import Stats from './Stats.svelte';
  import Canvas from './Canvas.svelte';

  let options = {
    "b": 2.0
  }

  let canvas;
  
  
</script>

<main>
	<div class="container-fluid">
		<div class="row">
		  <div class="col-3">
        <Options bind:options={options} />
        <Stats/>
		  </div>
		  <div class="col-9">
			  <ul class="list-group list-group-horizontal">
          <li class="list-group-item">
            <div class="form-check">
              <input class="form-check-input" type="checkbox" value="" id="defaultCheck1">
              <label class="form-check-label" for="defaultCheck1">
                Step-by-step
              </label>
            </div>
          </li>
          <li class="list-group-item">
            <select class="custom-select">
              <option value="volvo" selected>Volvo</option>
              <option value="saab">Saab</option>
              <option value="mercedes">Mercedes</option>
              <option value="audi">Audi</option>
            </select>
          </li>
          <li class="list-group-item">
            <button type="button" class="btn btn-primary">Primary</button>
          </li>
          <li class="list-group-item">
            <span class="badge badge-secondary">34 / 35</span>
          </li>
        </ul>
        <Canvas bind:this={canvas} posX={options.b}/>
        <button type="button" class="btn btn-primary" on:click={canvas.redraw()}>Start</button>
		  </div>
		</div>
	</div>
</main>

<style>
  .card {
    margin-bottom: 20px;
  }
</style>