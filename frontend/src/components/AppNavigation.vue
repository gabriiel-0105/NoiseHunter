<template>
 <v-navigation-drawer permanent width="228">
  <v-list-item class="mt-4 mb-2" title="NoiseHunter" subtitle="Desafio Liga Jovem" prepend-icon="mdi-shield-wave" />
  <v-divider />
  <v-list nav density="comfortable" class="mt-3">
   <v-list-item title="Visão geral" prepend-icon="mdi-view-dashboard-outline" to="/" exact />
   <v-list-item title="Usuários" prepend-icon="mdi-account-group-outline" to="/usuarios" />
   <v-list-item title="Dispositivos" prepend-icon="mdi-access-point" to="/dispositivos" />
   <v-list-item title="Ocorrências" prepend-icon="mdi-waveform" to="/eventos" />
   <v-list-item title="Leituras ESP32" prepend-icon="mdi-microphone-outline" to="/leituras" />
  </v-list>
  <template #append><div class="pa-4 text-caption text-medium-emphasis">Banco: MySQL<br/>Cadastros manuais e leituras do ESP32 (amplitudes não calibradas).</div></template>
 </v-navigation-drawer>
</template>
