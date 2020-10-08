<template>
  <div class="hello">
    <h1>{{ msg }}</h1>
    <ul>
      <li>a: <input v-model="config_a" type="range" min="1.0" max="10.0" step="0.01" value="2.0" class="slider" id="myRange"> {{ config_a }}</li>
      <button type="button" class="btn btn-primary">Primary</button>
    </ul>
  </div>
</template>

<script>
export default {
  name: "HelloWorld",
  props: {
    msg: String
  },
  data: function () {
    return {
      config_a: 2.0
    }
  }
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
h3 {
  margin: 40px 0 0;
}
ul {
  list-style-type: none;
  padding: 0;
}
li {
  display: inline-block;
  margin: 0 10px;
}
a {
  color: #42b983;
}
</style>
