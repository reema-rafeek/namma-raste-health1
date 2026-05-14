package com.namma.raste.health.ui.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash_route")
    object Onboarding : Screen("onboarding_route")
    object Main : Screen("main_root_route")
    
    // Sub-routes for Main TabHost
    object Dashboard : Screen("dashboard_view_route")
    object RoadDirectory : Screen("road_directory_view_route")
    object SuccessMap : Screen("success_map_view_route")
    object ReportHistory : Screen("report_history_view_route")

    // Full screens
    object RoadDetail : Screen("road_detail_route/{roadId}") {
        fun createRoute(roadId: Int) = "road_detail_route/$roadId"
    }
    object DamageReport : Screen("damage_report_route/{roadId}") {
        fun createRoute(roadId: Int) = "damage_report_route/$roadId"
    }
    object ContractorProfile : Screen("contractor_route/{contractorId}") {
        fun createRoute(contractorId: Int) = "contractor_route/$contractorId"
    }
    object AdminSeed : Screen("admin_seed_route")
}
