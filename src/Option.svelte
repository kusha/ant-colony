<script>
  export let name;
  export let defaultValue;
  export let max;
  export let min;
  export let step;
  export let description = null;
  export let value = defaultValue;
</script>

<li class="list-group-item">
  <h4>{name} <span class="badge badge-pill badge-primary">{value}</span></h4>
  <div class="form-group">
    {#if description}
    <label for="option-{name}">{description}</label>
    {/if}
    <input type="range" max={max} min={min} step={step} class="form-control-range" id="option-{name}" bind:value={value}>
  </div>
</li>