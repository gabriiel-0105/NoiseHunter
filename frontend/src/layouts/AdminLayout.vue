<template>
 <v-app>
  <AppHeader/><AppNavigation/>
  <v-main class="bg-grey-lighten-4">
   <v-container fluid class="pa-6 pa-md-8"><slot/></v-container>
  </v-main>
 </v-app>
</template>
