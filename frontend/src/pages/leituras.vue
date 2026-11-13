<template>
  <AdminLayout>
    <div class="d-flex flex-wrap justify-space-between align-center ga-3 mb-4">
      <div>
        <h1 class="text-h4 font-weight-bold">Leituras do ESP32</h1>
        <p class="text-medium-emphasis mt-1">Amplitude real do MAX9814, recebida pelo Java e armazenada no MySQL.</p>
      </div>
      <v-btn color="primary" prepend-icon="mdi-refresh" :loading="carregando" @click="atualizar">Atualizar</v-btn>
    </div>

    
    <v-alert v-if="erro" type="error" variant="tonal" class="mb-4">{{ erro }}</v-alert>

    <v-row class="mb-3" align="center">
      <v-col cols="12" sm="6" md="4">
        <v-select
          v-model="filtro" :items="opcoesDispositivo" item-title="title" item-value="value"
          label="Filtrar por sensor" variant="outlined" density="comfortable" hide-details
        />
      </v-col>
      <v-col cols="12" sm="6" md="4">
        <v-chip prepend-icon="mdi-timer-refresh-outline" color="info" variant="tonal">
          Atualização automática a cada 5 s
        </v-chip>
      </v-col>
      <v-col cols="12" md="4" class="text-caption text-medium-emphasis">
        Última atualização: {{ formatarData(ultimaAtualizacao) }}
      </v-col>
    </v-row>

    <v-row class="mb-4">
      <v-col cols="12" sm="6" md="4">
        <v-card border rounded="xl" elevation="0" class="pa-5 h-100">
          <div class="text-caption text-medium-emphasis">Última amplitude recebida</div>
          <div class="text-h3 font-weight-bold mt-2">{{ ultima?.amplitudePp ?? '—' }}</div>
          <div class="text-caption">Unidades do ADC (pico a pico)</div>
        </v-card>
      </v-col>
      <v-col cols="12" sm="6" md="4">
        <v-card border rounded="xl" elevation="0" class="pa-5 h-100">
          <div class="text-caption text-medium-emphasis">Maior amplitude entre os registros exibidos</div>
          <div class="text-h3 font-weight-bold mt-2">{{ pico }}</div>
          <div class="text-caption">Máximo da seleção atual</div>
        </v-card>
      </v-col>
      <v-col cols="12" sm="6" md="4">
        <v-card border rounded="xl" elevation="0" class="pa-5 h-100">
          <div class="text-caption text-medium-emphasis">Picos acima do limite experimental</div>
          <div class="text-h3 font-weight-bold mt-2">{{ totalPicos }}</div>
          <div class="text-caption">Entre os últimos 100 registros retornados pela API</div>
        </v-card>
      </v-col>
    </v-row>

    <v-card rounded="xl" border elevation="0" class="pa-5 mb-5">
      <div class="text-h6 mb-1">Últimas amplitudes</div>
      <div class="text-caption text-medium-emphasis mb-4">Cada barra representa o máximo do intervalo enviado pelo ESP32.</div>
      <div v-if="!leiturasFiltradas.length" class="text-medium-emphasis py-8 text-center">Nenhuma leitura recebida ainda.</div>
      <div v-for="l in recentes" :key="l.id" class="d-flex align-center ga-3 mb-3">
        <span class="text-caption" style="width: 66px; flex-shrink: 0">{{ l.amplitudePp }}</span>
        <div class="flex-grow-1 rounded" style="height: 15px; background: #E8F0F4; overflow: hidden">
          <div :style="{ width: `${Math.min(100, l.amplitudePp / 4095 * 100)}%`, background: l.classificacao === 'PICO_INTENSO' ? '#F59E0B' : '#0891B2', height: '100%' }" />
        </div>
        <span class="text-caption text-medium-emphasis" style="width: 105px; text-align: right">{{ hora(l.dataHora) }}</span>
      </div>
    </v-card>

    <v-card rounded="xl" border elevation="0">
      <v-card-title>Histórico de leituras</v-card-title>
      <v-data-table :headers="cabecalhos" :items="leiturasFiltradas" :loading="carregando" no-data-text="Nenhuma leitura encontrada.">
        <template #item.dispositivo="{ item }">{{ item.dispositivo?.nome || '—' }}</template>
        <template #item.dataHora="{ item }">{{ formatarData(item.dataHora) }}</template>
        <template #item.classificacao="{ item }">
          <v-chip size="small" :color="item.classificacao === 'PICO_INTENSO' ? 'warning' : 'info'" variant="tonal">
            {{ item.classificacao === 'PICO_INTENSO' ? 'Pico intenso' : 'Ambiente' }}
          </v-chip>
        </template>
      </v-data-table>
    </v-card>
  </AdminLayout>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { listarLeituras, listarDispositivos, formatarData } from '@/services/noisehunter_services'

const leituras = ref([])
const dispositivos = ref([])
const filtro = ref('todos')
const erro = ref('')
const carregando = ref(false)
const ultimaAtualizacao = ref(null)
let temporizador = null
const cabecalhos = [
  { title: 'Horário', key: 'dataHora' },
  { title: 'Sensor', key: 'dispositivo' },
  { title: 'Amplitude ADC', key: 'amplitudePp' },
  { title: 'Intervalo (ms)', key: 'intervaloMs' },
  { title: 'Classificação', key: 'classificacao' },
]
const opcoesDispositivo = computed(() => [
  { title: 'Todos os sensores', value: 'todos' },
  ...dispositivos.value.map(d => ({ title: `${d.nome} (#${d.id})`, value: d.id })),
])
const leiturasFiltradas = computed(() => filtro.value === 'todos'
  ? leituras.value
  : leituras.value.filter(l => Number(l.dispositivo?.id) === Number(filtro.value)))
const ultima = computed(() => leiturasFiltradas.value[0] ?? null)
const pico = computed(() => leiturasFiltradas.value.length
  ? Math.max(...leiturasFiltradas.value.map(l => l.amplitudePp)) : '—')
const totalPicos = computed(() => leiturasFiltradas.value.filter(l => l.classificacao === 'PICO_INTENSO').length)
const recentes = computed(() => leiturasFiltradas.value.slice(0, 12))
function hora(data) { return data ? new Date(data).toLocaleTimeString('pt-BR') : '—' }
async function atualizar() {
  if (carregando.value) return
  carregando.value = true
  try {
    const [ls, ds] = await Promise.all([listarLeituras(), listarDispositivos()])
    leituras.value = Array.isArray(ls) ? ls : []
    dispositivos.value = Array.isArray(ds) ? ds : []
    ultimaAtualizacao.value = new Date()
    erro.value = ''
  } catch (e) {
    erro.value = 'Não foi possível consultar /api/leituras. Verifique o backend e o MySQL.'
  } finally {
    carregando.value = false
  }
}
onMounted(() => {
  atualizar()
  temporizador = setInterval(atualizar, 5000)
})
onUnmounted(() => clearInterval(temporizador))
</script>
