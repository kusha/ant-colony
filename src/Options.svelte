<script>
import Option from './Option.svelte';
export let options = {};
let runOnUpdate = false;
</script>

<div class="card">
  <!--<div class="card-body">
    <h5 class="card-title">Options</h5>
     <h6 class="card-subtitle mb-2 text-muted">of the last run</h6>
    <p class="card-text">Some quick example text to build on the card title and make up the bulk of the card's content.</p>
  </div> -->
  <div class="card-header">
    Options
  </div>
  <ul class="list-group list-group-flush">
    <Option bind:value={options.b} name="b" defaultValue={3.2} min={1.0} max={10.0} step={0.1} description="test description"/>
  </ul>
  <div class="card-footer">
    <div class="form-check">
      <input bind:value={runOnUpdate} class="form-check-input" type="checkbox" id="run-on-update">
      <label class="form-check-label" for="run-on-update">
        Run on options update
      </label>
    </div>
  </div>
</div>
