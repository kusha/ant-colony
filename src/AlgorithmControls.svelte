<script>
import { createEventDispatcher } from 'svelte';
export let algorithm;

const dispatch = createEventDispatcher();

function requestRun() {
  dispatch('run');
}
</script>

<div class="form-row align-items-center">
  <div class="col-auto">
    <label for="next-step-btn">Select algorithm</label>
  </div>
  <div class="col-auto">
    <select class="custom-select" value={algorithm}>
      <option value="ant-cycle">ant-cycle</option>
      <option value="aco">aco</option>
    </select>
  </div>
  <div class="col-auto">
    <button type="button" class="btn btn-primary" on:click={requestRun()}>Start</button>
  </div>
</div>
