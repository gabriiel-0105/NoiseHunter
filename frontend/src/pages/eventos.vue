<template>
 <AdminLayout>
  <div class="d-flex flex-wrap justify-space-between align-center mb-5 ga-3">
   <div><h1 class="text-h4 font-weight-bold">Ocorrências sonoras</h1><p class="text-medium-emphasis mt-1">Registros de teste ligados à tabela <code>ocorrencias</code> do banco de dados.</p></div>
   <v-btn color="primary" prepend-icon="mdi-plus" :disabled="!dispositivos.length" @click="abrirNovo">Simular ocorrência</v-btn>
  </div>
  0
  <v-alert v-if="!dispositivos.length && !erro" class="mb-4" type="info" variant="tonal">Cadastre um dispositivo antes de criar ocorrências. <router-link to="/dispositivos">Cadastrar dispositivo</router-link></v-alert>
  <v-alert v-if="mensagem" type="success" class="mb-4" closable @click:close="mensagem=''">{{ mensagem }}</v-alert>
  <v-alert v-if="erro" type="error" class="mb-4" closable @click:close="erro=''">{{ erro }}</v-alert>
  <v-card rounded="lg" elevation="1">
   <v-card-title class="pa-4 d-flex align-center ga-4"><span>Histórico de ocorrências</span><v-spacer/><v-btn icon="mdi-refresh" variant="text" aria-label="Atualizar ocorrências" :loading="carregando" @click="carregar" /></v-card-title>
   <v-data-table :headers="headers" :items="eventos" :loading="carregando" no-data-text="Nenhuma ocorrência registrada.">
    <template #item.dispositivo="{item}">{{ item.dispositivo?.nome || '—' }}</template>
    <template #item.tipo="{item}"><v-chip :color="item.tipo==='DISPARO_SUSPEITO'?'error':item.tipo==='VEICULO_BARULHENTO'?'warning':'info'" size="small" variant="tonal">{{ nomesTipos[item.tipo] || item.tipo }}</v-chip></template>
    <template #item.decibeis="{item}"><strong>{{ item.decibeis }} dB</strong></template>
    <template #item.registradoEm="{item}">{{ formatarData(item.registradoEm) }}</template>
    <template #item.actions="{item}"><v-btn icon="mdi-delete" variant="text" color="error" aria-label="Excluir ocorrência" size="small" @click="excluir(item)" /></template>
   </v-data-table>
  </v-card>
  <v-dialog v-model="dialog" max-width="560"><v-card rounded="lg">
    <v-card-title class="pa-5">Simular nova ocorrência</v-card-title>
    <v-card-text><v-form ref="formulario" @submit.prevent="salvar">
     <v-select v-model="form.dispositivoId" :items="dispositivos" item-title="nome" item-value="id" label="Dispositivo" :rules="[v=>!!v||'Selecione o dispositivo']" required @update:model-value="atualizarLocalizacao" />
     <v-select v-model="form.tipoSom" :items="opcoesTipos" label="Tipo de som" :rules="[v=>!!v||'Selecione o tipo']" required />
     <v-text-field v-model.number="form.decibeis" type="number" step="0.01" min="0" max="200" label="Intensidade (dB)" suffix="dB" :rules="[v=>(v!==null&&v!==''&&Number.isFinite(Number(v))&&Number(v)>=0&&Number(v)<=200)||'Informe um valor entre 0 e 200']" required />
     <v-text-field v-model="form.localizacao" label="Localização da ocorrência" maxlength="100" hint="Se ficar vazio, será utilizada a localização do dispositivo." persistent-hint />
    </v-form></v-card-text>
    <v-card-actions class="pa-4"><v-spacer/><v-btn variant="text" @click="dialog=false">Cancelar</v-btn><v-btn color="primary" :loading="salvando" @click="salvar">Registrar simulação</v-btn></v-card-actions>
  </v-card></v-dialog>
 </AdminLayout>
</template>
<script setup>
import {ref,onMounted} from 'vue'
import {listarDispositivos,listarEventos,criarEvento,excluirEvento,nomesTipos,formatarData} from '@/services/noisehunter_services'
const opcoesTipos=Object.entries(nomesTipos).map(([value,title])=>({value,title}))
const headers=[{title:'Data e hora',key:'registradoEm'},{title:'Dispositivo',key:'dispositivo'},{title:'Tipo',key:'tipo'},{title:'Decibéis',key:'decibeis'},{title:'Localização',key:'localizacao'},{title:'Ações',key:'actions',sortable:false}]
const dispositivos=ref([]),eventos=ref([]),dialog=ref(false),formulario=ref(null),carregando=ref(false),salvando=ref(false),erro=ref(''),mensagem=ref('')
const vazio=()=>({dispositivoId:null,tipoSom:'VEICULO_BARULHENTO',decibeis:90,localizacao:''})
const form=ref(vazio())
async function carregar(){carregando.value=true;erro.value='';try{const [d,e]=await Promise.all([listarDispositivos(),listarEventos()]);dispositivos.value=d;eventos.value=e}catch(err){erro.value='Não foi possível carregar ocorrências. Verifique a API.'}finally{carregando.value=false}}
function abrirNovo(){form.value=vazio();dialog.value=true}
function atualizarLocalizacao(id){form.value.localizacao=dispositivos.value.find(d=>d.id===id)?.localizacao||''}
async function salvar(){const validacao=await formulario.value?.validate();if(!validacao?.valid)return;salvando.value=true;erro.value='';try{await criarEvento(form.value);dialog.value=false;form.value=vazio();mensagem.value='Ocorrência simulada registrada no banco.';await carregar()}catch(err){erro.value=err.response?.data?.mensagem||'Não foi possível registrar a ocorrência.'}finally{salvando.value=false}}
async function excluir(item){if(!confirm('Deseja excluir esta ocorrência?'))return;try{await excluirEvento(item.id);mensagem.value='Ocorrência removida.';await carregar()}catch(err){erro.value='Não foi possível excluir a ocorrência.'}}
onMounted(carregar)
</script>
