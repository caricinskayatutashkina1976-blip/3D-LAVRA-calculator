package com.lavka.calculator.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lavka.calculator.ui.theme.OrangePrimary

@Composable
fun AgreementScreen(
    isFirstLaunch: Boolean,
    onAccept: () -> Unit,
    onDecline: () -> Unit,
    onClose: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Text(
            text = "Пользовательское соглашение",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AgreementSection(
                    title = "1. Общие положения",
                    text = "Настоящее Пользовательское соглашение регулирует использование мобильного приложения «3D-LAVKA-calculator» (далее — Приложение). Устанавливая и используя Приложение, вы подтверждаете своё согласие с условиями данного соглашения."
                )
                AgreementSection(
                    title = "2. Назначение приложения",
                    text = "Приложение предназначено для расчёта ориентировочной стоимости 3D-печати на основе введённых пользователем параметров. Результаты расчёта носят рекомендательный характер и не являются коммерческим предложением."
                )
                AgreementSection(
                    title = "3. Ответственность",
                    text = "Разработчик не несёт ответственности за финансовые решения, принятые на основе расчётов Приложения. Пользователь самостоятельно определяет актуальные цены на материалы, электроэнергию, амортизацию оборудования и прочие расходы."
                )
                AgreementSection(
                    title = "4. Персональные данные",
                    text = "Приложение не собирает и не передаёт персональные данные пользователя. Все настройки и параметры расчёта хранятся локально на устройстве."
                )
                AgreementSection(
                    title = "5. Изменения",
                    text = "Разработчик оставляет за собой право обновлять Приложение и настоящее соглашение. Актуальная версия соглашения доступна в разделе «О приложении»."
                )
                AgreementSection(
                    title = "6. Контакты",
                    text = "По вопросам использования Приложения обращайтесь к администратору проекта 3D-LAVKA."
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isFirstLaunch) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onDecline,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Отклонить")
                }
                Button(
                    onClick = onAccept,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
                ) {
                    Text("Принять")
                }
            }
        } else {
            Button(
                onClick = onClose,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
            ) {
                Text("Закрыть")
            }
        }
    }
}

@Composable
private fun AgreementSection(title: String, text: String) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
